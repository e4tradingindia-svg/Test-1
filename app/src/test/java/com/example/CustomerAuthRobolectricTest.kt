package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.dao.CustomerDao
import com.example.data.dao.SalonDao
import com.example.data.database.SalonMateDatabase
import com.example.data.model.CustomerEntity
import com.example.data.model.SalonEntity
import com.example.data.repository.CustomerRepository
import com.example.data.repository.SalonRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CustomerAuthRobolectricTest {

    private lateinit var db: SalonMateDatabase
    private lateinit var customerDao: CustomerDao
    private lateinit var salonDao: SalonDao
    private lateinit var customerRepository: CustomerRepository
    private lateinit var salonRepository: SalonRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SalonMateDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        customerDao = db.customerDao()
        salonDao = db.salonDao()
        customerRepository = CustomerRepository(customerDao)
        salonRepository = SalonRepository(salonDao)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testCustomerRegistrationAndLogin() = runBlocking {
        val regResult = customerRepository.registerCustomer("Neha Sharma", "9876543210", "pass123")
        assertTrue(regResult.isSuccess)
        val customer = regResult.getOrThrow()
        assertEquals("Neha Sharma", customer.name)
        assertEquals("9876543210", customer.mobileNumber)
        assertEquals("9876543210", customer.normalizedPhone)

        // Duplicate mobile-number prevention
        val duplicateResult = customerRepository.registerCustomer("Another User", "9876543210", "pass456")
        assertTrue(duplicateResult.isFailure)

        val loginResult = customerRepository.loginCustomer("9876543210", "pass123")
        assertTrue(loginResult.isSuccess)
        assertEquals(customer.customerId, loginResult.getOrThrow().customerId)
    }

    @Test
    fun testLocalCustomerDaoNormalizedPhoneQueries() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val localDb = Room.inMemoryDatabaseBuilder(
            context,
            com.example.data.local.SalonMateDatabase::class.java
        ).allowMainThreadQueries().build()
        try {
            val localDao = localDb.customerDao()
            val customer = CustomerEntity(
                customerId = "CUST-556677",
                name = "Riya Kapoor",
                mobileNumber = "9876556677",
                normalizedPhone = "9876556677",
                passwordHash = "hash"
            )
            localDao.insertCustomer(customer)

            val byPhone = localDao.getCustomerByPhone("9876556677")
            assertNotNull(byPhone)
            assertEquals("CUST-556677", byPhone?.customerId)
            assertEquals("9876556677", byPhone?.normalizedPhone)
            assertEquals(1, localDao.countByPhone("9876556677"))
            assertEquals(0, localDao.countByPhone("9999999999"))
        } finally {
            localDb.close()
        }
    }

    @Test
    fun testSafeMigrationPreservesExistingCustomers() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbName = "migration_test_${System.currentTimeMillis()}.db"
        context.deleteDatabase(dbName)
        try {
            val legacyDb = context.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null)
            legacyDb.execSQL(
                """
                CREATE TABLE IF NOT EXISTS customers (
                    customerId TEXT NOT NULL PRIMARY KEY,
                    name TEXT NOT NULL,
                    mobileNumber TEXT NOT NULL,
                    passwordHash TEXT NOT NULL,
                    profilePhotoUri TEXT NOT NULL,
                    address TEXT NOT NULL,
                    createdAt INTEGER NOT NULL
                )
                """.trimIndent()
            )
            legacyDb.execSQL(
                """
                CREATE TABLE IF NOT EXISTS salons (
                    salonId TEXT NOT NULL PRIMARY KEY,
                    salonName TEXT NOT NULL,
                    ownerName TEXT NOT NULL,
                    mobileNumber TEXT NOT NULL,
                    passwordHash TEXT NOT NULL,
                    businessType TEXT NOT NULL,
                    bannerUri TEXT NOT NULL,
                    interiorPhotoUris TEXT NOT NULL,
                    address TEXT NOT NULL,
                    latitude REAL NOT NULL,
                    longitude REAL NOT NULL,
                    rating REAL NOT NULL,
                    reviewCount INTEGER NOT NULL,
                    isActive INTEGER NOT NULL,
                    createdAt INTEGER NOT NULL
                )
                """.trimIndent()
            )
            legacyDb.execSQL(
                """
                CREATE TABLE IF NOT EXISTS salon_services (
                    serviceId TEXT NOT NULL PRIMARY KEY,
                    salonId TEXT NOT NULL,
                    serviceName TEXT NOT NULL,
                    category TEXT NOT NULL,
                    price REAL NOT NULL,
                    durationMinutes INTEGER NOT NULL,
                    description TEXT NOT NULL,
                    isAvailable INTEGER NOT NULL
                )
                """.trimIndent()
            )
            legacyDb.execSQL(
                """
                CREATE TABLE IF NOT EXISTS salon_staff (
                    staffId TEXT NOT NULL PRIMARY KEY,
                    salonId TEXT NOT NULL,
                    name TEXT NOT NULL,
                    role TEXT NOT NULL,
                    isAvailable INTEGER NOT NULL
                )
                """.trimIndent()
            )
            legacyDb.execSQL(
                """
                CREATE TABLE IF NOT EXISTS salon_business_hours (
                    id TEXT NOT NULL PRIMARY KEY,
                    salonId TEXT NOT NULL,
                    dayOfWeek INTEGER NOT NULL,
                    openTime TEXT NOT NULL,
                    closeTime TEXT NOT NULL,
                    isOpen INTEGER NOT NULL
                )
                """.trimIndent()
            )
            legacyDb.execSQL(
                """
                CREATE TABLE IF NOT EXISTS salon_bookings (
                    bookingId TEXT NOT NULL PRIMARY KEY,
                    salonId TEXT NOT NULL,
                    customerId TEXT NOT NULL,
                    customerName TEXT NOT NULL,
                    customerPhone TEXT NOT NULL,
                    serviceIds TEXT NOT NULL,
                    serviceNames TEXT NOT NULL,
                    bookingDate TEXT NOT NULL,
                    timeSlot TEXT NOT NULL,
                    totalAmount REAL NOT NULL,
                    status TEXT NOT NULL,
                    createdAt INTEGER NOT NULL
                )
                """.trimIndent()
            )
            legacyDb.execSQL(
                """
                CREATE TABLE IF NOT EXISTS owner_notifications (
                    notificationId TEXT NOT NULL PRIMARY KEY,
                    salonId TEXT NOT NULL,
                    title TEXT NOT NULL,
                    message TEXT NOT NULL,
                    timestamp INTEGER NOT NULL,
                    isRead INTEGER NOT NULL
                )
                """.trimIndent()
            )
            legacyDb.execSQL(
                "INSERT INTO customers (customerId, name, mobileNumber, passwordHash, profilePhotoUri, address, createdAt) VALUES ('CUST-112233', 'Priya', '9811112233', 'hash123', '', 'Delhi', 1000)"
            )
            legacyDb.version = 1
            legacyDb.close()

            val migratedDb = Room.databaseBuilder(context, SalonMateDatabase::class.java, dbName)
                .addMigrations(SalonMateDatabase.MIGRATION_1_2)
                .allowMainThreadQueries()
                .build()
            val migratedCustomer = runBlocking { migratedDb.customerDao().getCustomerByMobile("9811112233") }
            assertNotNull(migratedCustomer)
            assertEquals("CUST-112233", migratedCustomer?.customerId)
            assertEquals("Priya", migratedCustomer?.name)
            assertEquals("9811112233", migratedCustomer?.normalizedPhone)
            migratedDb.close()
        } finally {
            context.deleteDatabase(dbName)
        }
    }

    @Test
    fun testCustomerProfileEditAndPersistence() = runBlocking {
        val reg = customerRepository.registerCustomer("Aman Verma", "9811122233", "secret12")
        val custId = reg.getOrThrow().customerId

        customerRepository.updateProfile(custId, "file:///data/user/0/profile_1.jpg", "Sector 62, Noida")
        val updated = customerRepository.getCustomer(custId)
        assertNotNull(updated)
        assertEquals("file:///data/user/0/profile_1.jpg", updated?.profilePhotoUri)
        assertEquals("Sector 62, Noida", updated?.address)
        assertEquals("Aman Verma", updated?.name)
        assertEquals("9811122233", updated?.mobileNumber)
    }

    @Test
    fun testSalonOwnerRegistrationAndDashboardName() = runBlocking {
        // Test with custom salon name "Maa Salon" as requested in user requirements
        val reg = salonRepository.registerOwner(
            salonName = "Maa Salon",
            ownerName = "Rajesh Gupta",
            mobileNumber = "9988776655",
            password = "ownerpass",
            businessType = "Unisex Salon"
        )
        assertTrue(reg.isSuccess)
        val salon = reg.getOrThrow()
        assertEquals("Maa Salon", salon.salonName)
        assertEquals("Rajesh Gupta", salon.ownerName)

        val fetched = salonRepository.getSalonById(salon.salonId)
        assertNotNull(fetched)
        assertEquals("Maa Salon", fetched?.salonName)
        assertFalse(fetched?.salonName == "Elite Studio & Spa")
    }

    @Test
    fun testActivePartnerToggleAndBookingRestriction() = runBlocking {
        val reg = salonRepository.registerOwner("Glow Studio", "Sneha", "9822334455", "1234", "Women's Beauty Parlour")
        val salonId = reg.getOrThrow().salonId

        // Initially active
        val initialSalon = salonRepository.getSalonById(salonId)
        assertTrue(initialSalon?.isActive == true)

        // Toggle to Deactivated
        val newStatus = salonRepository.toggleActiveStatus(salonId)
        assertFalse(newStatus)
        val deactivatedSalon = salonRepository.getSalonById(salonId)
        assertFalse(deactivatedSalon?.isActive == true)

        // Attempt booking while deactivated -> must fail
        val bookingAttempt = salonRepository.createBooking(
            salonId = salonId,
            customerId = "CUST-1",
            customerName = "Test Client",
            customerPhone = "9999999999",
            serviceIds = listOf("srv-1"),
            serviceNames = listOf("Hair Spa"),
            bookingDate = "2026-11-15",
            timeSlot = "10:00 AM",
            totalAmount = 500.0
        )
        assertTrue(bookingAttempt.isFailure)
        assertTrue(bookingAttempt.exceptionOrNull()?.message?.contains("DEACTIVATED") == true)

        // Reactivate
        salonRepository.setActiveStatus(salonId, true)
        val reactivatedBooking = salonRepository.createBooking(
            salonId = salonId,
            customerId = "CUST-1",
            customerName = "Test Client",
            customerPhone = "9999999999",
            serviceIds = listOf("srv-1"),
            serviceNames = listOf("Hair Spa"),
            bookingDate = "2026-11-15",
            timeSlot = "10:00 AM",
            totalAmount = 500.0
        )
        assertTrue(reactivatedBooking.isSuccess)
    }

    @Test
    fun testSalonInteriorPhotoGalleryUploadPersistenceAndCustomerViewing() = runBlocking {
        // 1. Create a salon for testing interior gallery
        val reg = salonRepository.registerOwner("Elite Haven", "Kunal", "9123456789", "pass123", "Men's Salon")
        val salonId = reg.getOrThrow().salonId

        // Initial state: No interior photos uploaded -> empty list
        val initialSalon = salonRepository.getSalonById(salonId)
        assertNotNull(initialSalon)
        assertEquals(0, initialSalon!!.getInteriorPhotoList().size)

        // 2. Owner uploads 4 interior photos
        val uploadedUris = listOf(
            "file:///data/user/0/interior_1.jpg",
            "file:///data/user/0/interior_2.jpg",
            "file:///data/user/0/interior_3.jpg",
            "file:///data/user/0/interior_4.jpg"
        )
        salonRepository.updateSalonBusinessProfile(
            salonId = salonId,
            bannerUri = "file:///data/user/0/banner.jpg",
            interiorPhotoUris = uploadedUris.joinToString(","),
            address = "Plot 10, Civil Lines",
            mobileNumber = "9123456789",
            lat = 28.55,
            lng = 77.25
        )

        // Verify photos persisted and parsed correctly
        val updatedSalon = salonRepository.getSalonById(salonId)
        assertNotNull(updatedSalon)
        val photosList = updatedSalon!!.getInteriorPhotoList()
        assertEquals(4, photosList.size)
        assertEquals("file:///data/user/0/interior_1.jpg", photosList[0])
        assertEquals("file:///data/user/0/interior_2.jpg", photosList[1])
        assertEquals("file:///data/user/0/interior_3.jpg", photosList[2])
        assertEquals("file:///data/user/0/interior_4.jpg", photosList[3])

        // 3. Test replacing an interior photo at index 1
        val replacedList = photosList.toMutableList()
        replacedList[1] = "file:///data/user/0/interior_2_replaced.jpg"
        salonRepository.updateSalonBusinessProfile(
            salonId = salonId,
            bannerUri = updatedSalon.bannerUri,
            interiorPhotoUris = replacedList.joinToString(","),
            address = updatedSalon.address,
            mobileNumber = updatedSalon.mobileNumber,
            lat = updatedSalon.latitude,
            lng = updatedSalon.longitude
        )
        val afterReplace = salonRepository.getSalonById(salonId)
        val afterReplacePhotos = afterReplace!!.getInteriorPhotoList()
        assertEquals(4, afterReplacePhotos.size)
        assertEquals("file:///data/user/0/interior_2_replaced.jpg", afterReplacePhotos[1])

        // 4. Test deleting an interior photo (delete index 3, leaves 3 photos)
        val afterDeleteList = afterReplacePhotos.toMutableList().apply { removeAt(3) }
        salonRepository.updateSalonBusinessProfile(
            salonId = salonId,
            bannerUri = updatedSalon.bannerUri,
            interiorPhotoUris = afterDeleteList.joinToString(","),
            address = updatedSalon.address,
            mobileNumber = updatedSalon.mobileNumber,
            lat = updatedSalon.latitude,
            lng = updatedSalon.longitude
        )
        val afterDeleteSalon = salonRepository.getSalonById(salonId)
        val afterDeletePhotos = afterDeleteSalon!!.getInteriorPhotoList()
        assertEquals(3, afterDeletePhotos.size)

        // 5. Test another salon without interior photos: ensure empty list returned so gallery is hidden
        val emptySalonReg = salonRepository.registerOwner("Plain Salon", "Anil", "9000000001", "1234", "Unisex Salon")
        val emptySalon = salonRepository.getSalonById(emptySalonReg.getOrThrow().salonId)
        assertTrue(emptySalon!!.getInteriorPhotoList().isEmpty())
    }

    @Test
    fun testBookingCreationAndDoubleBookingPrevention() = runBlocking {
        val reg = salonRepository.registerOwner("Luxury Cuts", "Rohan", "9871112233", "pass", "Men's Salon")
        val salonId = reg.getOrThrow().salonId

        // First booking
        val booking1 = salonRepository.createBooking(
            salonId = salonId,
            customerId = "CUST-001",
            customerName = "Kavita",
            customerPhone = "9876543210",
            serviceIds = listOf("srv-1", "srv-2"),
            serviceNames = listOf("Haircut", "Facial"),
            bookingDate = "2026-12-01",
            timeSlot = "02:30 PM",
            totalAmount = 800.0
        )
        assertTrue(booking1.isSuccess)

        // Second booking for the same salon, same date, and SAME time slot -> double booking prevented!
        val booking2 = salonRepository.createBooking(
            salonId = salonId,
            customerId = "CUST-002",
            customerName = "Rahul",
            customerPhone = "9112233445",
            serviceIds = listOf("srv-3"),
            serviceNames = listOf("Beard Sculpting"),
            bookingDate = "2026-12-01",
            timeSlot = "02:30 PM",
            totalAmount = 300.0
        )
        assertTrue(booking2.isFailure)
        assertTrue(booking2.exceptionOrNull()?.message?.contains("already reserved") == true)

        // Booking for different slot succeeds
        val booking3 = salonRepository.createBooking(
            salonId = salonId,
            customerId = "CUST-002",
            customerName = "Rahul",
            customerPhone = "9112233445",
            serviceIds = listOf("srv-3"),
            serviceNames = listOf("Beard Sculpting"),
            bookingDate = "2026-12-01",
            timeSlot = "04:00 PM",
            totalAmount = 300.0
        )
        assertTrue(booking3.isSuccess)
    }
}
