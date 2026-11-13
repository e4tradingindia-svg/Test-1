package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.ui.theme.SalonMateTheme
import com.example.ui.welcome.WelcomeScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WelcomeScreenResponsiveRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun verifyScreenFitsWithoutScrolling(screenLabel: String) {
        var customerClicks = 0
        var ownerClicks = 0

        composeTestRule.setContent {
            SalonMateTheme {
                WelcomeScreen(
                    onCustomerClick = { customerClicks++ },
                    onOwnerClick = { ownerClicks++ }
                )
            }
        }
        composeTestRule.waitForIdle()

        val rootNode = composeTestRule.onNodeWithTag("welcome_screen_root")
        val rootBounds = rootNode.fetchSemanticsNode().boundsInRoot

        val logoNode = composeTestRule.onNodeWithTag("salonmate_logo_badge")
        val customerBtnNode = composeTestRule.onNodeWithTag("continue_as_customer_button")
        val ownerBtnNode = composeTestRule.onNodeWithTag("continue_as_owner_button")
        val footerNode = composeTestRule.onNodeWithTag("footer_privacy_policy")
        val customerPhotoNode = composeTestRule.onNodeWithContentDescription(
            "SalonMate Customers - Haircut Client and Spa Facial Client"
        )
        val ownerPhotoNode = composeTestRule.onNodeWithContentDescription(
            "SalonMate Professionals - Stylists and Beauty Experts in Uniform"
        )

        logoNode.assertIsDisplayed()
        customerBtnNode.assertIsDisplayed()
        ownerBtnNode.assertIsDisplayed()
        footerNode.assertIsDisplayed()
        customerPhotoNode.assertIsDisplayed()
        ownerPhotoNode.assertIsDisplayed()

        // Verify all elements fit inside the viewport without scrolling or clipping
        listOf(
            "logo" to logoNode.fetchSemanticsNode().boundsInRoot,
            "customer_btn" to customerBtnNode.fetchSemanticsNode().boundsInRoot,
            "owner_btn" to ownerBtnNode.fetchSemanticsNode().boundsInRoot,
            "footer" to footerNode.fetchSemanticsNode().boundsInRoot,
            "customer_photo" to customerPhotoNode.fetchSemanticsNode().boundsInRoot,
            "owner_photo" to ownerPhotoNode.fetchSemanticsNode().boundsInRoot
        ).forEach { (name, bounds) ->
            assertTrue(
                "$name top (${bounds.top}) < root top (${rootBounds.top}) on $screenLabel",
                bounds.top >= rootBounds.top - 1f
            )
            assertTrue(
                "$name bottom (${bounds.bottom}) > root bottom (${rootBounds.bottom}) on $screenLabel",
                bounds.bottom <= rootBounds.bottom + 1f
            )
            assertTrue(
                "$name right (${bounds.right}) > root right (${rootBounds.right}) on $screenLabel",
                bounds.right <= rootBounds.right + 1f
            )
        }

        // Verify Continue buttons work directly without scrolling
        customerBtnNode.performClick()
        ownerBtnNode.performClick()
        assertEquals(1, customerClicks)
        assertEquals(1, ownerClicks)
    }

    @Test
    @Config(qualifiers = "w360dp-h600dp-xxhdpi")
    fun compactPhoneScreen_360x600_showsBothPortalsAndButtonsWithoutScrolling() {
        verifyScreenFitsWithoutScrolling("360x600dp")
    }

    @Test
    @Config(qualifiers = "w393dp-h740dp-xxhdpi")
    fun mediumPhoneScreen_393x740_showsBothPortalsAndButtonsWithoutScrolling() {
        verifyScreenFitsWithoutScrolling("393x740dp")
    }

    @Test
    @Config(qualifiers = "w412dp-h860dp-xxhdpi")
    fun tallPhoneScreen_412x860_showsBothPortalsAndButtonsWithoutScrolling() {
        verifyScreenFitsWithoutScrolling("412x860dp")
    }
}
