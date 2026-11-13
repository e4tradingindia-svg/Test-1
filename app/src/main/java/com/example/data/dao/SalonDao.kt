package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.OwnerNotificationEntity
import com.example.data.model.SalonBookingEntity
import com.example.data.model.SalonBusinessHoursEntity
import com.example.data.model.SalonEntity
import com.example.data.model.SalonServiceEntity
import com.example.data.model.SalonStaffEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SalonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSalon(salon: SalonEntity)

    @Update
    suspend fun updateSalon(salon: SalonEntity)

    @Query("SELECT * FROM salons ORDER BY rating DESC")
    suspend fun getAllSalons(): List<SalonEntity>

    @Query("SELECT * FROM salons ORDER BY rating DESC")
    fun observeAllSalons(): Flow<List<SalonEntity>>

    @Query("SELECT * FROM salons WHERE salonId = :salonId LIMIT 1")
    suspend fun getSalonById(salonId: String): SalonEntity?

    @Query("SELECT * FROM salons WHERE salonId = :salonId LIMIT 1")
    fun observeSalonById(salonId: String): Flow<SalonEntity?>

    @Query("SELECT * FROM salons WHERE mobileNumber = :phone LIMIT 1")
    suspend fun getSalonByPhone(phone: String): SalonEntity?

    @Query("SELECT * FROM salons WHERE salonId = :idOrPhone OR mobileNumber = :idOrPhone LIMIT 1")
    suspend fun getSalonByIdOrPhone(idOrPhone: String): SalonEntity?

    @Query("""
        UPDATE salons 
        SET bannerUri = :bannerUri, 
            interiorPhotoUris = :interiorPhotoUris,
            address = :address, 
            mobileNumber = :mobileNumber, 
            latitude = :lat, 
            longitude = :lng 
        WHERE salonId = :salonId
    """)
    suspend fun updateBusinessProfile(
        salonId: String,
        bannerUri: String,
        interiorPhotoUris: String,
        address: String,
        mobileNumber: String,
        lat: Double,
        lng: Double
    )

    @Query("UPDATE salons SET isActive = :isActive WHERE salonId = :salonId")
    suspend fun updateActiveStatus(salonId: String, isActive: Boolean)

    // Services
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<SalonServiceEntity>)

    @Query("SELECT * FROM salon_services WHERE salonId = :salonId")
    suspend fun getServicesBySalonId(salonId: String): List<SalonServiceEntity>

    @Query("SELECT * FROM salon_services WHERE salonId = :salonId")
    fun observeServicesBySalonId(salonId: String): Flow<List<SalonServiceEntity>>

    // Staff
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaff(staff: List<SalonStaffEntity>)

    @Query("SELECT * FROM salon_staff WHERE salonId = :salonId")
    suspend fun getStaffBySalonId(salonId: String): List<SalonStaffEntity>

    @Query("SELECT * FROM salon_staff WHERE salonId = :salonId")
    fun observeStaffBySalonId(salonId: String): Flow<List<SalonStaffEntity>>

    // Business Hours
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusinessHours(hours: List<SalonBusinessHoursEntity>)

    @Query("SELECT * FROM salon_business_hours WHERE salonId = :salonId")
    suspend fun getHoursBySalonId(salonId: String): List<SalonBusinessHoursEntity>

    @Query("SELECT * FROM salon_business_hours WHERE salonId = :salonId")
    fun observeHoursBySalonId(salonId: String): Flow<List<SalonBusinessHoursEntity>>

    // Bookings
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBooking(booking: SalonBookingEntity)

    @Query("SELECT * FROM salon_bookings WHERE salonId = :salonId AND bookingDate = :date AND status != 'CANCELLED'")
    suspend fun getBookingsForSalonAndDate(salonId: String, date: String): List<SalonBookingEntity>

    @Query("SELECT * FROM salon_bookings WHERE salonId = :salonId ORDER BY createdAt DESC")
    fun observeBookingsBySalonId(salonId: String): Flow<List<SalonBookingEntity>>

    @Query("SELECT * FROM salon_bookings WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun observeBookingsByCustomerId(customerId: String): Flow<List<SalonBookingEntity>>

    // Notifications
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: OwnerNotificationEntity)

    @Query("SELECT * FROM owner_notifications WHERE salonId = :salonId ORDER BY timestamp DESC")
    fun observeNotificationsBySalonId(salonId: String): Flow<List<OwnerNotificationEntity>>

    @Query("UPDATE owner_notifications SET isRead = 1 WHERE notificationId = :notificationId")
    suspend fun markNotificationRead(notificationId: String)
}
