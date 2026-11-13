package com.example.data.repository

import com.example.data.dao.SalonDao
import com.example.data.model.OwnerNotificationEntity
import com.example.data.model.SalonBookingEntity
import com.example.data.model.SalonBusinessHoursEntity
import com.example.data.model.SalonEntity
import com.example.data.model.SalonServiceEntity
import com.example.data.model.SalonStaffEntity
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest
import java.util.UUID

class SalonRepository(private val salonDao: SalonDao) {

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun seedDefaultsIfNeeded() {
        val count = salonDao.getAllSalons().size
        if (count > 0) return

        val initialSalons = listOf(
            SalonEntity(
                salonId = "SLN-100001",
                salonName = "Luxe Unisex Salon & Spa",
                ownerName = "Vikram Mehta",
                mobileNumber = "9876543210",
                passwordHash = hashPassword("1234"),
                businessType = "Unisex Salon",
                address = "Level 2, Phoenix Grand Mall, Connaught Place",
                latitude = 28.6315,
                longitude = 77.2167,
                rating = 4.9,
                reviewCount = 312,
                isActive = true
            ),
            SalonEntity(
                salonId = "SLN-100002",
                salonName = "Royal Men's Grooming Lounge",
                ownerName = "Kabir Singh",
                mobileNumber = "9876543211",
                passwordHash = hashPassword("1234"),
                businessType = "Men's Salon",
                address = "Shop 14, Defence Colony Market",
                latitude = 28.5744,
                longitude = 77.2343,
                rating = 4.8,
                reviewCount = 185,
                isActive = true
            ),
            SalonEntity(
                salonId = "SLN-100003",
                salonName = "Bliss Women's Beauty Parlour",
                ownerName = "Pooja Sharma",
                mobileNumber = "9876543212",
                passwordHash = hashPassword("1234"),
                businessType = "Women's Beauty Parlour",
                address = "Plot 88, Greater Kailash 1",
                latitude = 28.5535,
                longitude = 77.2390,
                rating = 4.7,
                reviewCount = 240,
                isActive = true
            )
        )

        for (salon in initialSalons) {
            salonDao.insertOrUpdateSalon(salon)
            seedServicesAndStaff(salon.salonId, salon.businessType)
        }
    }

    private suspend fun seedServicesAndStaff(salonId: String, businessType: String) {
        val services = when (businessType) {
            "Men's Salon" -> listOf(
                SalonServiceEntity(UUID.randomUUID().toString(), salonId, "Signature Royal Haircut", "Hair", 350.0, 30),
                SalonServiceEntity(UUID.randomUUID().toString(), salonId, "Beard Sculpting & Styling", "Beard", 250.0, 25),
                SalonServiceEntity(UUID.randomUUID().toString(), salonId, "Charcoal Detox Facial", "Facial", 650.0, 45),
                SalonServiceEntity(UUID.randomUUID().toString(), salonId, "Head & Shoulder Massage", "Spa", 499.0, 30),
                SalonServiceEntity(UUID.randomUUID().toString(), salonId, "Hair Color & Highlights", "Hair", 899.0, 60),
                SalonServiceEntity(UUID.randomUUID().toString(), salonId, "Express Manicure", "Nails", 450.0, 30)
            )
            "Women's Beauty Parlour" -> listOf(
                SalonServiceEntity(UUID.randomUUID().toString(), salonId, "Designer Layer Haircut", "Hair", 550.0, 45),
                SalonServiceEntity(UUID.randomUUID().toString(), salonId, "Gold Glow Bridal Facial", "Facial", 1200.0, 60),
                SalonServiceEntity(UUID.randomUUID().toString(), salonId, "Party Glam Makeup", "Makeup", 1800.0, 60),
                SalonServiceEntity(UUID.randomUUID().toString(), salonId, "Gel Nail Art & Extensions", "Nails", 950.0, 45),
                SalonServiceEntity(UUID.randomUUID().toString(), salonId, "Aromatherapy Full Body Spa", "Spa", 2200.0, 90),
                SalonServiceEntity(UUID.randomUUID().toString(), salonId, "Hydra Moisture Hair Spa", "Hair", 850.0, 45)
            )
            else -> listOf(
                SalonServiceEntity(UUID.randomUUID().toString(), salonId, "Precision Hair Styling", "Hair", 450.0, 35),
                SalonServiceEntity(UUID.randomUUID().toString(), salonId, "O3+ Radiance Facial", "Facial", 950.0, 50),
                SalonServiceEntity(UUID.randomUUID().toString(), salonId, "Beard Styling & Shave", "Beard", 300.0, 25),
                SalonServiceEntity(UUID.randomUUID().toString(), salonId, "Deluxe Pedicure & Manicure", "Nails", 750.0, 45),
                SalonServiceEntity(UUID.randomUUID().toString(), salonId, "Moroccan Hair Spa", "Hair", 999.0, 50),
                SalonServiceEntity(UUID.randomUUID().toString(), salonId, "Ayurvedic Relaxing Spa", "Spa", 1500.0, 60)
            )
        }
        salonDao.insertServices(services)

        val staff = listOf(
            SalonStaffEntity(UUID.randomUUID().toString(), salonId, "Master Stylist Rahul", "Senior Stylist"),
            SalonStaffEntity(UUID.randomUUID().toString(), salonId, "Skin Specialist Ananya", "Esthetician"),
            SalonStaffEntity(UUID.randomUUID().toString(), salonId, "Therapist Arjun", "Spa Therapist")
        )
        salonDao.insertStaff(staff)

        val hours = (1..7).map { day ->
            SalonBusinessHoursEntity(
                id = "${salonId}_$day",
                salonId = salonId,
                dayOfWeek = day,
                openTime = "09:00",
                closeTime = "21:00",
                isOpen = true
            )
        }
        salonDao.insertBusinessHours(hours)
    }

    suspend fun registerOwner(
        salonName: String,
        ownerName: String,
        mobileNumber: String,
        password: String,
        businessType: String
    ): Result<SalonEntity> {
        val trimmedMobile = mobileNumber.trim()
        val trimmedSalon = salonName.trim()
        val trimmedOwner = ownerName.trim()

        if (trimmedSalon.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter the salon or parlour name"))
        }
        if (trimmedOwner.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter the owner name"))
        }
        if (trimmedMobile.length < 10) {
            return Result.failure(IllegalArgumentException("Please enter a valid 10-digit mobile number"))
        }
        if (password.length < 4) {
            return Result.failure(IllegalArgumentException("Password must be at least 4 characters"))
        }

        val existing = salonDao.getSalonByPhone(trimmedMobile)
        if (existing != null) {
            return Result.failure(IllegalStateException("A salon with this mobile number is already registered"))
        }

        val salonId = "SLN-" + trimmedMobile.takeLast(6)
        val salon = SalonEntity(
            salonId = salonId,
            salonName = trimmedSalon,
            ownerName = trimmedOwner,
            mobileNumber = trimmedMobile,
            passwordHash = hashPassword(password),
            businessType = businessType,
            address = "Main Commercial Plaza, Sector 18",
            latitude = 28.5700,
            longitude = 77.3200,
            rating = 5.0,
            reviewCount = 1,
            isActive = true
        )
        salonDao.insertOrUpdateSalon(salon)
        seedServicesAndStaff(salonId, businessType)
        return Result.success(salon)
    }

    suspend fun loginOwner(idOrPhone: String, password: String): Result<SalonEntity> {
        val trimmed = idOrPhone.trim()
        val salon = salonDao.getSalonByIdOrPhone(trimmed)
            ?: return Result.failure(IllegalArgumentException("No salon found with this ID or Mobile Number"))

        val hashed = hashPassword(password)
        if (salon.passwordHash != hashed) {
            return Result.failure(IllegalArgumentException("Incorrect password"))
        }
        return Result.success(salon)
    }

    fun observeAllSalons(): Flow<List<SalonEntity>> = salonDao.observeAllSalons()

    suspend fun getSalonById(salonId: String): SalonEntity? = salonDao.getSalonById(salonId)

    fun observeSalonById(salonId: String): Flow<SalonEntity?> = salonDao.observeSalonById(salonId)

    suspend fun updateSalonBusinessProfile(
        salonId: String,
        bannerUri: String,
        interiorPhotoUris: String,
        address: String,
        mobileNumber: String,
        lat: Double,
        lng: Double
    ) {
        salonDao.updateBusinessProfile(
            salonId = salonId,
            bannerUri = bannerUri,
            interiorPhotoUris = interiorPhotoUris,
            address = address,
            mobileNumber = mobileNumber,
            lat = lat,
            lng = lng
        )
    }

    suspend fun toggleActiveStatus(salonId: String): Boolean {
        val salon = salonDao.getSalonById(salonId) ?: return false
        val newStatus = !salon.isActive
        salonDao.updateActiveStatus(salonId, newStatus)
        return newStatus
    }

    suspend fun setActiveStatus(salonId: String, isActive: Boolean) {
        salonDao.updateActiveStatus(salonId, isActive)
    }

    fun observeServices(salonId: String): Flow<List<SalonServiceEntity>> =
        salonDao.observeServicesBySalonId(salonId)

    suspend fun getServices(salonId: String): List<SalonServiceEntity> =
        salonDao.getServicesBySalonId(salonId)

    suspend fun getStaff(salonId: String): List<SalonStaffEntity> =
        salonDao.getStaffBySalonId(salonId)

    suspend fun getHours(salonId: String): List<SalonBusinessHoursEntity> =
        salonDao.getHoursBySalonId(salonId)

    suspend fun getBookingsForDate(salonId: String, date: String): List<SalonBookingEntity> =
        salonDao.getBookingsForSalonAndDate(salonId, date)

    fun observeBookingsBySalon(salonId: String): Flow<List<SalonBookingEntity>> =
        salonDao.observeBookingsBySalonId(salonId)

    fun observeBookingsByCustomer(customerId: String): Flow<List<SalonBookingEntity>> =
        salonDao.observeBookingsByCustomerId(customerId)

    suspend fun createBooking(
        salonId: String,
        customerId: String,
        customerName: String,
        customerPhone: String,
        serviceIds: List<String>,
        serviceNames: List<String>,
        bookingDate: String,
        timeSlot: String,
        totalAmount: Double
    ): Result<SalonBookingEntity> {
        val salon = salonDao.getSalonById(salonId)
            ?: return Result.failure(IllegalStateException("Salon not found"))

        if (!salon.isActive) {
            return Result.failure(IllegalStateException("This salon is currently DEACTIVATED and not accepting bookings"))
        }

        // Check double booking
        val existingBookings = salonDao.getBookingsForSalonAndDate(salonId, bookingDate)
        val isDoubleBooked = existingBookings.any { it.timeSlot.equals(timeSlot, ignoreCase = true) }
        if (isDoubleBooked) {
            return Result.failure(IllegalStateException("This time slot ($timeSlot) is already reserved. Please choose another slot."))
        }

        val booking = SalonBookingEntity(
            bookingId = "BK-" + UUID.randomUUID().toString().take(8).uppercase(),
            salonId = salonId,
            customerId = customerId,
            customerName = customerName,
            customerPhone = customerPhone,
            serviceIds = serviceIds.joinToString(","),
            serviceNames = serviceNames.joinToString(", "),
            bookingDate = bookingDate,
            timeSlot = timeSlot,
            totalAmount = totalAmount,
            status = "CONFIRMED"
        )
        salonDao.insertBooking(booking)

        // Notify salon owner
        val notification = OwnerNotificationEntity(
            notificationId = UUID.randomUUID().toString(),
            salonId = salonId,
            title = "New Booking Confirmed!",
            message = "Customer $customerName booked ${serviceNames.joinToString(", ")} on $bookingDate at $timeSlot (₹${totalAmount.toInt()})"
        )
        salonDao.insertNotification(notification)

        return Result.success(booking)
    }

    fun observeNotifications(salonId: String): Flow<List<OwnerNotificationEntity>> =
        salonDao.observeNotificationsBySalonId(salonId)

    suspend fun markNotificationRead(notificationId: String) =
        salonDao.markNotificationRead(notificationId)

    suspend fun getSalonByPhone(phone: String): SalonEntity? =
        salonDao.getSalonByPhone(phone)

    suspend fun getSalonByIdOrPhone(salonId: String, phone: String): SalonEntity? =
        salonDao.getSalonById(salonId) ?: salonDao.getSalonByPhone(phone)

    suspend fun updateSalonActiveStatus(salonId: String, isActive: Boolean) =
        salonDao.updateActiveStatus(salonId, isActive)

    fun observeServicesForSalon(salonId: String): Flow<List<SalonServiceEntity>> =
        salonDao.observeServicesBySalonId(salonId)

    fun observeStaffForSalon(salonId: String): Flow<List<SalonStaffEntity>> =
        salonDao.observeStaffBySalonId(salonId)

    fun observeBookingsForSalon(salonId: String): Flow<List<SalonBookingEntity>> =
        salonDao.observeBookingsBySalonId(salonId)

    fun observeBookingsForDate(salonId: String, date: String): Flow<List<SalonBookingEntity>> =
        salonDao.observeBookingsForSalonAndDate(salonId, date)

    fun observeNotificationsForSalon(salonId: String): Flow<List<OwnerNotificationEntity>> =
        salonDao.observeNotificationsBySalonId(salonId)

    suspend fun updateSalonBusinessProfile(
        salonId: String,
        bannerUri: String?,
        address: String?,
        mobileNumber: String,
        latitude: Double?,
        longitude: Double?
    ): Result<SalonEntity> {
        val existing = salonDao.getSalonById(salonId)
            ?: return Result.failure(IllegalStateException("Salon not found"))
        salonDao.updateBusinessProfile(
            salonId = salonId,
            bannerUri = bannerUri.orEmpty(),
            interiorPhotoUris = existing.interiorPhotoUris,
            address = address.orEmpty().trim(),
            mobileNumber = mobileNumber.trim(),
            lat = latitude ?: existing.latitude,
            lng = longitude ?: existing.longitude
        )
        val updated = salonDao.getSalonById(salonId)
            ?: return Result.failure(IllegalStateException("Failed to reload salon profile"))
        return Result.success(updated)
    }

    suspend fun createBooking(
        salonId: String,
        customerId: String,
        customerName: String,
        customerPhone: String,
        selectedServices: List<SalonServiceEntity>,
        bookingDate: String,
        timeSlot: String,
        staffId: String? = null,
        staffName: String? = null
    ): Result<SalonBookingEntity> = createBooking(
        salonId = salonId,
        customerId = customerId,
        customerName = customerName,
        customerPhone = customerPhone,
        serviceIds = selectedServices.map { it.serviceId },
        serviceNames = selectedServices.map { it.serviceName },
        bookingDate = bookingDate,
        timeSlot = timeSlot,
        totalAmount = selectedServices.sumOf { it.price }
    )
}

