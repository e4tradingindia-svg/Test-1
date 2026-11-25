package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.SalonMateDatabase
import com.example.data.repository.CustomerRepository
import com.example.data.repository.SalonRepository
import com.example.data.session.SessionManager
import com.example.ui.screens.customer.CustomerMyBookingsScreen
import com.example.ui.screens.customer.CustomerPortalScreen
import com.example.ui.theme.SalonMateTheme
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CustomerMyBookingsRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var db: SalonMateDatabase
    private lateinit var customerRepository: CustomerRepository
    private lateinit var salonRepository: SalonRepository
    private lateinit var sessionManager: SessionManager

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
    fun customerPortal_threeDotMenu_showsMyBookingsAndTriggersNavigation() {
        var myBookingsClicks = 0

        composeTestRule.setContent {
            SalonMateTheme {
                CustomerPortalScreen(
                    customerId = "CUST-1001",
                    customerRepository = customerRepository,
                    salonRepository = salonRepository,
                    sessionManager = sessionManager,
                    onNavigateToEditProfile = {},
                    onNavigateToSalonDetails = {},
                    onNavigateToMyBookings = { myBookingsClicks++ },
                    onLogout = {}
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("btn_customer_portal_menu")
            .assertIsDisplayed()
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("menu_item_my_bookings")
            .assertIsDisplayed()
            .performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, myBookingsClicks)
    }

    @Test
    fun myBookingsScreen_whenCustomerHasNoBookings_showsEmptyStateAndBackNavigates() {
        var backClicks = 0

        composeTestRule.setContent {
            SalonMateTheme {
                CustomerMyBookingsScreen(
                    customerId = "CUST-EMPTY",
                    salonRepository = salonRepository,
                    onNavigateBack = { backClicks++ }
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("my_bookings_empty_state").assertIsDisplayed()
        composeTestRule.onNodeWithText("No Bookings Yet").assertIsDisplayed()

        composeTestRule.onNodeWithTag("btn_my_bookings_back")
            .assertIsDisplayed()
            .performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, backClicks)
    }

    @Test
    fun myBookingsScreen_displaysCurrentCustomerBookingsWithResolvedSalonNameAndDetails() = runBlocking {
        val salonA = salonRepository.registerOwner(
            salonName = "Maa Salon",
            ownerName = "Rajesh",
            mobileNumber = "9876500001",
            password = "pass",
            businessType = "Unisex Salon"
        ).getOrThrow()

        val salonB = salonRepository.registerOwner(
            salonName = "Other Salon",
            ownerName = "Meera",
            mobileNumber = "9876500002",
            password = "pass",
            businessType = "Women's Beauty Parlour"
        ).getOrThrow()

        val bookingA = salonRepository.createBooking(
            salonId = salonA.salonId,
            customerId = "CUST-A",
            customerName = "Aarav",
            customerPhone = "9111111111",
            serviceIds = listOf("s1", "s2"),
            serviceNames = listOf("Haircut", "Beard Trim"),
            bookingDate = "2026-11-28",
            timeSlot = "02:30 PM",
            totalAmount = 650.0
        ).getOrThrow()

        val bookingB = salonRepository.createBooking(
            salonId = salonB.salonId,
            customerId = "CUST-B",
            customerName = "Neha",
            customerPhone = "9222222222",
            serviceIds = listOf("s3"),
            serviceNames = listOf("Gold Facial"),
            bookingDate = "2026-11-29",
            timeSlot = "04:00 PM",
            totalAmount = 1200.0
        ).getOrThrow()

        composeTestRule.setContent {
            SalonMateTheme {
                CustomerMyBookingsScreen(
                    customerId = "CUST-A",
                    salonRepository = salonRepository,
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("customer_booking_card_${bookingA.bookingId}").assertIsDisplayed()
        composeTestRule.onNodeWithText("Maa Salon").assertIsDisplayed()
        composeTestRule.onNodeWithText("Haircut, Beard Trim").assertIsDisplayed()
        composeTestRule.onNodeWithText("2026-11-28").assertIsDisplayed()
        composeTestRule.onNodeWithText("02:30 PM").assertIsDisplayed()
        composeTestRule.onNodeWithText("₹650").assertIsDisplayed()
        composeTestRule.onNodeWithText("CONFIRMED").assertIsDisplayed()

        // Ensure another customer's booking is not shown
        composeTestRule.onNodeWithTag("customer_booking_card_${bookingB.bookingId}").assertDoesNotExist()
    }
}
