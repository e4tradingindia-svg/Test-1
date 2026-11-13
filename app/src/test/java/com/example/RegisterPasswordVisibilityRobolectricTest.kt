package com.example

import android.content.Context
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.SalonMateDatabase
import com.example.data.repository.CustomerRepository
import com.example.data.repository.SalonRepository
import com.example.data.session.SessionManager
import com.example.ui.screens.customer.CustomerRegisterScreen
import com.example.ui.screens.owner.SalonOwnerRegisterScreen
import com.example.ui.theme.SalonMateTheme
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RegisterPasswordVisibilityRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var db: SalonMateDatabase
    private lateinit var customerRepository: CustomerRepository
    private lateinit var salonRepository: SalonRepository
    private lateinit var sessionManager: SessionManager

    private val isPasswordMasked = SemanticsMatcher.keyIsDefined(SemanticsProperties.Password)
    private val isPasswordVisible = SemanticsMatcher.keyNotDefined(SemanticsProperties.Password)

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SalonMateDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        customerRepository = CustomerRepository(db.customerDao())
        salonRepository = SalonRepository(db.salonDao())
        sessionManager = SessionManager(context)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun customerSignUp_passwordHiddenByDefault_andTogglesOnEyeClick() {
        composeTestRule.setContent {
            SalonMateTheme {
                CustomerRegisterScreen(
                    customerRepository = customerRepository,
                    sessionManager = sessionManager,
                    onRegisterSuccess = {},
                    onNavigateToLogin = {},
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()

        val passwordInput = composeTestRule.onNodeWithTag("customer_register_password_input")
        val toggleButton = composeTestRule.onNodeWithTag("btn_customer_register_toggle_password")

        passwordInput.performScrollTo()
        composeTestRule.waitForIdle()

        // Default: hidden
        passwordInput.assert(isPasswordMasked)
        toggleButton.assertIsDisplayed()
        toggleButton.assert(hasContentDescription("Show password"))

        // Type password while hidden
        passwordInput.performTextInput("MySecret99")
        composeTestRule.waitForIdle()
        passwordInput.assert(isPasswordMasked)

        // Tap eye icon -> show typed password
        toggleButton.performClick()
        composeTestRule.waitForIdle()
        passwordInput.assert(isPasswordVisible)
        passwordInput.assert(hasText("MySecret99"))
        toggleButton.assert(hasContentDescription("Hide password"))

        // Tap again -> hide password
        toggleButton.performClick()
        composeTestRule.waitForIdle()
        passwordInput.assert(isPasswordMasked)
        toggleButton.assert(hasContentDescription("Show password"))
    }

    @Test
    fun ownerRegistration_passwordHiddenByDefault_andTogglesOnEyeClick() {
        composeTestRule.setContent {
            SalonMateTheme {
                SalonOwnerRegisterScreen(
                    salonRepository = salonRepository,
                    sessionManager = sessionManager,
                    onRegisterSuccess = {},
                    onNavigateToLogin = {},
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()

        val passwordInput = composeTestRule.onNodeWithTag("owner_register_password_input")
        val toggleButton = composeTestRule.onNodeWithTag("btn_owner_register_toggle_password")

        passwordInput.performScrollTo()
        composeTestRule.waitForIdle()

        // Default: hidden
        passwordInput.assert(isPasswordMasked)
        toggleButton.assertIsDisplayed()
        toggleButton.assert(hasContentDescription("Show password"))

        // Type password while hidden
        passwordInput.performTextInput("OwnerPass123")
        composeTestRule.waitForIdle()
        passwordInput.assert(isPasswordMasked)

        // Tap eye icon -> show typed password
        toggleButton.performClick()
        composeTestRule.waitForIdle()
        passwordInput.assert(isPasswordVisible)
        passwordInput.assert(hasText("OwnerPass123"))
        toggleButton.assert(hasContentDescription("Hide password"))

        // Tap again -> hide password
        toggleButton.performClick()
        composeTestRule.waitForIdle()
        passwordInput.assert(isPasswordMasked)
        toggleButton.assert(hasContentDescription("Show password"))
    }
}
