package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.SalonMateDatabase
import com.example.data.repository.CustomerRepository
import com.example.data.security.PasswordHasher
import com.example.data.session.CustomerSessionManager
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CustomerAuthRobolectricTest {

  private lateinit var context: Context
  private lateinit var database: SalonMateDatabase
  private lateinit var repository: CustomerRepository
  private lateinit var sessionManager: CustomerSessionManager

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    database = Room.inMemoryDatabaseBuilder(
      context,
      SalonMateDatabase::class.java
    ).allowMainThreadQueries().build()
    repository = CustomerRepository(database.customerDao())
    sessionManager = CustomerSessionManager(context)
    sessionManager.clearSession()
  }

  @After
  fun tearDown() {
    sessionManager.clearSession()
    database.close()
  }

  @Test
  fun testSuccessfulRegistrationAndSignIn() = runBlocking {
    val regResult = repository.registerCustomer(
      fullName = "Elena Vance",
      rawPhone = "+1 (555) 234-5678",
      password = "SecurePassword1!",
      confirmPassword = "SecurePassword1!"
    )
    assertTrue("Registration should succeed", regResult.isSuccess)
    val customer = regResult.getOrThrow()

    // Assert customer ID and security attributes
    assertTrue(customer.customerId.startsWith("CUST-"))
    assertEquals("Elena Vance", customer.fullName)
    assertEquals("5552345678", customer.normalizedPhone)
    assertNotEquals("SecurePassword1!", customer.passwordHash)
    assertEquals(64, customer.passwordHash.length) // SHA-256 hex is 64 chars
    assertEquals(32, customer.passwordSalt.length) // 16 bytes hex is 32 chars

    // Test Sign In with correct credentials
    val authResult = repository.authenticateCustomer(
      rawPhone = "555-234-5678",
      password = "SecurePassword1!"
    )
    assertTrue("Authentication should succeed", authResult.isSuccess)
    val authenticated = authResult.getOrThrow()
    assertEquals(customer.customerId, authenticated.customerId)
    assertEquals("Elena Vance", authenticated.fullName)
  }

  @Test
  fun testIncorrectPasswordAndInvalidInputs() = runBlocking {
    // 1. Blank full name
    val emptyNameResult = repository.registerCustomer(
      fullName = "   ",
      rawPhone = "1234567890",
      password = "password123",
      confirmPassword = "password123"
    )
    assertTrue(emptyNameResult.isFailure)
    assertEquals("Please enter your full name.", emptyNameResult.exceptionOrNull()?.message)

    // 2. Short phone number
    val invalidPhoneResult = repository.registerCustomer(
      fullName = "Test User",
      rawPhone = "12345",
      password = "password123",
      confirmPassword = "password123"
    )
    assertTrue(invalidPhoneResult.isFailure)
    assertTrue(invalidPhoneResult.exceptionOrNull()?.message?.contains("valid mobile number") == true)

    // 3. Short password
    val shortPassResult = repository.registerCustomer(
      fullName = "Test User",
      rawPhone = "1234567890",
      password = "123",
      confirmPassword = "123"
    )
    assertTrue(shortPassResult.isFailure)
    assertEquals("Password must be at least 6 characters.", shortPassResult.exceptionOrNull()?.message)

    // 4. Mismatched passwords
    val mismatchResult = repository.registerCustomer(
      fullName = "Test User",
      rawPhone = "1234567890",
      password = "password123",
      confirmPassword = "differentPassword"
    )
    assertTrue(mismatchResult.isFailure)
    assertEquals("Passwords do not match.", mismatchResult.exceptionOrNull()?.message)

    // Register a valid customer
    repository.registerCustomer(
      fullName = "John Doe",
      rawPhone = "9876543210",
      password = "validPassword1",
      confirmPassword = "validPassword1"
    )

    // 5. Wrong password during sign in
    val wrongPassResult = repository.authenticateCustomer(
      rawPhone = "9876543210",
      password = "wrongPassword"
    )
    assertTrue(wrongPassResult.isFailure)
    assertEquals("Invalid mobile number or password.", wrongPassResult.exceptionOrNull()?.message)

    // 6. Non-existent phone number
    val wrongPhoneResult = repository.authenticateCustomer(
      rawPhone = "0000000000",
      password = "validPassword1"
    )
    assertTrue(wrongPhoneResult.isFailure)
    assertEquals("Invalid mobile number or password.", wrongPhoneResult.exceptionOrNull()?.message)
  }

  @Test
  fun testDuplicateMobileNumberRegistration() = runBlocking {
    val firstReg = repository.registerCustomer(
      fullName = "First Customer",
      rawPhone = "+1 (800) 555-0199",
      password = "PassWord123",
      confirmPassword = "PassWord123"
    )
    assertTrue("First registration should succeed", firstReg.isSuccess)

    // Attempt second registration with same normalized digits formatted differently
    val duplicateReg = repository.registerCustomer(
      fullName = "Second Customer",
      rawPhone = "1-800-555-0199",
      password = "AnotherPassword456",
      confirmPassword = "AnotherPassword456"
    )
    assertTrue("Duplicate registration must fail", duplicateReg.isFailure)
    assertTrue(
      duplicateReg.exceptionOrNull()?.message?.contains("already exists") == true
    )
  }

  @Test
  fun testAppRestartAndSessionRestoration() = runBlocking {
    val reg = repository.registerCustomer(
      fullName = "Marcus Cole",
      rawPhone = "5551122334",
      password = "Password99!",
      confirmPassword = "Password99!"
    ).getOrThrow()

    // Test with Remember Me = true
    sessionManager.saveSession(reg.customerId, rememberMe = true)

    // Simulate app restart with a fresh SessionManager instance
    val restartedSession = CustomerSessionManager(context)
    val restoredId = restartedSession.getPersistedCustomerId()
    assertEquals(reg.customerId, restoredId)

    val restoredCustomer = repository.getCustomerById(restoredId!!)
    assertNotNull(restoredCustomer)
    assertEquals("Marcus Cole", restoredCustomer?.fullName)

    // Test with Remember Me = false
    sessionManager.saveSession(reg.customerId, rememberMe = false)
    val restartedSessionNoRemember = CustomerSessionManager(context)
    assertNull(restartedSessionNoRemember.getPersistedCustomerId())
  }

  @Test
  fun testLogoutAndSubsequentSignIn() = runBlocking {
    val reg = repository.registerCustomer(
      fullName = "Diana Prince",
      rawPhone = "5559988776",
      password = "DianaSecret1",
      confirmPassword = "DianaSecret1"
    ).getOrThrow()

    sessionManager.saveSession(reg.customerId, rememberMe = true)
    assertEquals(reg.customerId, sessionManager.getPersistedCustomerId())

    // Log out
    sessionManager.clearSession()
    assertNull(sessionManager.getPersistedCustomerId())

    // Ensure database record is NOT deleted
    val existingCustomer = repository.getCustomerById(reg.customerId)
    assertNotNull("Customer account must be preserved in database after logout", existingCustomer)
    assertEquals("Diana Prince", existingCustomer?.fullName)

    // Subsequent sign-in succeeds
    val reAuth = repository.authenticateCustomer("5559988776", "DianaSecret1")
    assertTrue(reAuth.isSuccess)
    assertEquals(reg.customerId, reAuth.getOrThrow().customerId)
  }

  @Test
  fun testTwoDifferentCustomerAccountsRemainSeparate() = runBlocking {
    val customer1 = repository.registerCustomer(
      fullName = "Customer Alpha",
      rawPhone = "5551010101",
      password = "PasswordAlpha1",
      confirmPassword = "PasswordAlpha1"
    ).getOrThrow()

    val customer2 = repository.registerCustomer(
      fullName = "Customer Beta",
      rawPhone = "5552020202",
      password = "PasswordBeta2",
      confirmPassword = "PasswordBeta2"
    ).getOrThrow()

    // Distinct customer IDs
    assertNotEquals(customer1.customerId, customer2.customerId)
    assertNotEquals(customer1.normalizedPhone, customer2.normalizedPhone)

    // Authenticating Alpha gives Alpha's data only
    val auth1 = repository.authenticateCustomer("5551010101", "PasswordAlpha1").getOrThrow()
    assertEquals("Customer Alpha", auth1.fullName)
    assertEquals(customer1.customerId, auth1.customerId)

    // Authenticating Beta gives Beta's data only
    val auth2 = repository.authenticateCustomer("5552020202", "PasswordBeta2").getOrThrow()
    assertEquals("Customer Beta", auth2.fullName)
    assertEquals(customer2.customerId, auth2.customerId)

    // Cross-password authentication fails
    val crossAuth1 = repository.authenticateCustomer("5551010101", "PasswordBeta2")
    assertTrue("Alpha's account cannot be accessed with Beta's password", crossAuth1.isFailure)

    val crossAuth2 = repository.authenticateCustomer("5552020202", "PasswordAlpha1")
    assertTrue("Beta's account cannot be accessed with Alpha's password", crossAuth2.isFailure)
  }
}
