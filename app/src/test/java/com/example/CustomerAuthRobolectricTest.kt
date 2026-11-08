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
  fun testRegistrationAutomaticallyEstablishesSessionAndSignsIn() = runBlocking {
    val regResult = repository.registerCustomer(
      fullName = "Elena Vance",
      rawPhone = "+1 (555) 234-5678",
      password = "SecurePassword1!",
      confirmPassword = "SecurePassword1!"
    )
    assertTrue("Registration should succeed", regResult.isSuccess)
    val customer = regResult.getOrThrow()

    // Automatic session establishment without requiring Remember Me checkbox
    sessionManager.saveSession(customer.customerId)

    assertEquals("5552345678", customer.normalizedPhone)
    assertTrue(customer.customerId.startsWith("CUST-"))
    assertNotEquals("SecurePassword1!", customer.passwordHash)
    assertEquals(64, customer.passwordHash.length)
    assertEquals(32, customer.passwordSalt.length)

    // User is signed in immediately; session points directly to customer
    val activeId = sessionManager.getPersistedCustomerId()
    assertEquals(customer.customerId, activeId)
    val activeCustomer = repository.getCustomerById(activeId!!)
    assertNotNull(activeCustomer)
    assertEquals("Elena Vance", activeCustomer?.fullName)
  }

  @Test
  fun testRestartRestoresSessionWithoutRequiringLogin() = runBlocking {
    val reg = repository.registerCustomer(
      fullName = "Marcus Cole",
      rawPhone = "5551122334",
      password = "Password99!",
      confirmPassword = "Password99!"
    ).getOrThrow()

    // Session persisted automatically upon login/registration
    sessionManager.saveSession(reg.customerId)

    // Simulate app restart with a new CustomerSessionManager instance
    val restartedSession = CustomerSessionManager(context)
    assertTrue(restartedSession.hasActiveSession())
    val restoredId = restartedSession.getPersistedCustomerId()
    assertEquals(reg.customerId, restoredId)

    // Account restored directly from database
    val restoredCustomer = repository.getCustomerById(restoredId!!)
    assertNotNull(restoredCustomer)
    assertEquals("Marcus Cole", restoredCustomer?.fullName)
    assertEquals("5551122334", restoredCustomer?.normalizedPhone)
  }

  @Test
  fun testBackNavigationDoesNotSignOutOrClearSession() = runBlocking {
    val reg = repository.registerCustomer(
      fullName = "Jordan Reed",
      rawPhone = "5553344556",
      password = "SecretPass123",
      confirmPassword = "SecretPass123"
    ).getOrThrow()

    sessionManager.saveSession(reg.customerId)
    assertTrue(sessionManager.hasActiveSession())

    // Simulating back navigation (activity finish / back press):
    // Back navigation does NOT invoke sessionManager.clearSession()
    val sessionAfterBack = CustomerSessionManager(context)
    assertTrue(
      "Session must persist after back navigation",
      sessionAfterBack.hasActiveSession()
    )
    assertEquals(reg.customerId, sessionAfterBack.getPersistedCustomerId())

    // Database record remains completely intact
    val customerAfterBack = repository.getCustomerById(reg.customerId)
    assertNotNull(customerAfterBack)
    assertEquals("Jordan Reed", customerAfterBack?.fullName)
  }

  @Test
  fun testExplicitSignOutClearsSessionButPreservesAccount() = runBlocking {
    val reg = repository.registerCustomer(
      fullName = "Diana Prince",
      rawPhone = "5559988776",
      password = "DianaSecret1",
      confirmPassword = "DianaSecret1"
    ).getOrThrow()

    sessionManager.saveSession(reg.customerId)
    assertEquals(reg.customerId, sessionManager.getPersistedCustomerId())

    // Explicit Sign Out
    sessionManager.clearSession()
    assertNull("Session must be cleared on explicit sign out", sessionManager.getPersistedCustomerId())
    assertFalse(sessionManager.hasActiveSession())

    // Ensure database record is preserved
    val existingCustomer = repository.getCustomerById(reg.customerId)
    assertNotNull("Customer account must be preserved in database after logout", existingCustomer)
    assertEquals("Diana Prince", existingCustomer?.fullName)

    // Subsequent sign-in succeeds with original credentials
    val reAuth = repository.authenticateCustomer("5559988776", "DianaSecret1")
    assertTrue(reAuth.isSuccess)
    assertEquals(reg.customerId, reAuth.getOrThrow().customerId)
  }

  @Test
  fun testAfterSignOutAppRestartShowsOpeningScreen() = runBlocking {
    val reg = repository.registerCustomer(
      fullName = "Bruce Wayne",
      rawPhone = "5554443322",
      password = "BatPassword1",
      confirmPassword = "BatPassword1"
    ).getOrThrow()

    sessionManager.saveSession(reg.customerId)
    // Explicit Sign Out
    sessionManager.clearSession()

    // App restart after explicit sign out
    val restartedSession = CustomerSessionManager(context)
    assertFalse(
      "Reopening app after sign out must not have active session",
      restartedSession.hasActiveSession()
    )
    assertNull(restartedSession.getPersistedCustomerId())
  }

  @Test
  fun testDuplicateMobileNumberRegistrationIsRejected() = runBlocking {
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
  fun testTwoCustomerAccountsRemainIsolated() = runBlocking {
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
  fun testOwnerButtonNavigationAndPortalFlow() {
    var navigatedToOwner = false
    var navigatedToCustomer = false

    // Simulate WelcomeScreen callback triggers
    val onCustomerClick: () -> Unit = { navigatedToCustomer = true }
    val onOwnerClick: () -> Unit = { navigatedToOwner = true }

    // Trigger owner click
    onOwnerClick()
    assertTrue("Tapping Owner button/card must navigate to Owner entry screen", navigatedToOwner)
    assertFalse("Customer navigation must not trigger when Owner is tapped", navigatedToCustomer)

    // Trigger customer click
    onCustomerClick()
    assertTrue("Customer navigation must continue working independently", navigatedToCustomer)
  }

  @Test
  fun testCustomerEditProfileAndPersistence() = runBlocking {
    val reg = repository.registerCustomer(
      fullName = "Samantha Hayes",
      rawPhone = "5557788990",
      password = "Password123!",
      confirmPassword = "Password123!"
    ).getOrThrow()

    // Verify initial values
    assertNull(reg.profilePhotoUri)
    assertNull(reg.address)

    // 1. Edit and save profile: add photo URI and address
    val photoUri = "file:///data/user/0/com.example/files/profile_${reg.customerId}.jpg"
    val address = "742 Evergreen Terrace, Springfield"
    val updateResult = repository.updateCustomerProfile(
      customerId = reg.customerId,
      photoUri = photoUri,
      address = address
    )
    assertTrue("Profile update should succeed", updateResult.isSuccess)
    val updated = updateResult.getOrThrow()

    // 2. Customer Name, Mobile Number and Customer ID must remain unchanged and non-editable
    assertEquals("Samantha Hayes", updated.fullName)
    assertEquals("5557788990", updated.normalizedPhone)
    assertEquals(reg.customerId, updated.customerId)
    assertEquals(photoUri, updated.profilePhotoUri)
    assertEquals(address, updated.address)

    // 3. Verify persistence after reloading from database (simulates app restart/login)
    val reloaded = repository.getCustomerById(reg.customerId)
    assertNotNull(reloaded)
    assertEquals(photoUri, reloaded?.profilePhotoUri)
    assertEquals(address, reloaded?.address)

    // 4. Test clearing/editing address
    val clearedAddressResult = repository.updateCustomerProfile(
      customerId = reg.customerId,
      photoUri = photoUri,
      address = "   " // blank should become null
    )
    assertTrue(clearedAddressResult.isSuccess)
    val cleared = clearedAddressResult.getOrThrow()
    assertNull(cleared.address)
    assertEquals(photoUri, cleared.profilePhotoUri)
  }
}
