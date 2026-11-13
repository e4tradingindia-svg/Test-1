package com.example.data.local

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

  @Query("SELECT * FROM salons WHERE salonId = :salonId LIMIT 1")
  suspend fun getSalonById(salonId: String): SalonEntity?

  @Query("SELECT * FROM salons WHERE salonId = :salonId OR mobileNumber = :phone ORDER BY createdAt DESC LIMIT 1")
  suspend fun getSalonByIdOrPhone(salonId: String, phone: String): SalonEntity?

  @Query("SELECT * FROM salons WHERE mobileNumber = :phone ORDER BY createdAt DESC LIMIT 1")
  suspend fun getSalonByPhone(phone: String): SalonEntity?

  @Query("SELECT * FROM salons WHERE salonId = :salonId LIMIT 1")
  fun observeSalonById(salonId: String): Flow<SalonEntity?>

  @Query("SELECT * FROM salons ORDER BY createdAt DESC")
  suspend fun getAllSalons(): List<SalonEntity>

  @Query("SELECT * FROM salons ORDER BY createdAt DESC")
  fun observeAllSalons(): Flow<List<SalonEntity>>

  @Query("SELECT COUNT(*) FROM salons WHERE salonId = :salonId")
  suspend fun countById(salonId: String): Int

  @Query("UPDATE salons SET bannerUri = :bannerUri, address = :address, mobileNumber = :mobileNumber, latitude = :latitude, longitude = :longitude WHERE salonId = :salonId")
  suspend fun updateBusinessProfile(
    salonId: String,
    bannerUri: String?,
    address: String?,
    mobileNumber: String,
    latitude: Double?,
    longitude: Double?
  )

  @Query("UPDATE salons SET isActive = :isActive WHERE salonId = :salonId")
  suspend fun updateSalonActiveStatus(salonId: String, isActive: Boolean)

  // ─── SERVICES ──────────────────────────────────────────
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertServices(services: List<SalonServiceEntity>)

  @Query("SELECT * FROM salon_services WHERE salonId = :salonId ORDER BY price ASC")
  suspend fun getServicesForSalon(salonId: String): List<SalonServiceEntity>

  @Query("SELECT * FROM salon_services WHERE salonId = :salonId ORDER BY price ASC")
  fun observeServicesForSalon(salonId: String): Flow<List<SalonServiceEntity>>

  @Query("SELECT COUNT(*) FROM salon_services WHERE salonId = :salonId")
  suspend fun countServicesForSalon(salonId: String): Int

  // ─── STAFF ─────────────────────────────────────────────
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStaffList(staff: List<SalonStaffEntity>)

  @Query("SELECT * FROM salon_staff WHERE salonId = :salonId AND isAvailable = 1")
  suspend fun getStaffForSalon(salonId: String): List<SalonStaffEntity>

  @Query("SELECT * FROM salon_staff WHERE salonId = :salonId AND isAvailable = 1")
  fun observeStaffForSalon(salonId: String): Flow<List<SalonStaffEntity>>

  @Query("SELECT COUNT(*) FROM salon_staff WHERE salonId = :salonId")
  suspend fun countStaffForSalon(salonId: String): Int

  // ─── BUSINESS HOURS ────────────────────────────────────
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBusinessHours(hours: SalonBusinessHoursEntity)

  @Query("SELECT * FROM salon_business_hours WHERE salonId = :salonId LIMIT 1")
  suspend fun getBusinessHours(salonId: String): SalonBusinessHoursEntity?

  // ─── BOOKINGS & DOUBLE-BOOKING PREVENTION ───────────────
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBooking(booking: SalonBookingEntity)

  @Query("SELECT * FROM salon_bookings WHERE salonId = :salonId ORDER BY createdAt DESC")
  suspend fun getBookingsForSalon(salonId: String): List<SalonBookingEntity>

  @Query("SELECT * FROM salon_bookings WHERE salonId = :salonId ORDER BY createdAt DESC")
  fun observeBookingsForSalon(salonId: String): Flow<List<SalonBookingEntity>>

  @Query("SELECT * FROM salon_bookings WHERE salonId = :salonId AND bookingDate = :date AND status != 'CANCELLED'")
  suspend fun getBookingsForDate(salonId: String, date: String): List<SalonBookingEntity>

  @Query("SELECT * FROM salon_bookings WHERE salonId = :salonId AND bookingDate = :date AND status != 'CANCELLED'")
  fun observeBookingsForDate(salonId: String, date: String): Flow<List<SalonBookingEntity>>

  @Query("SELECT COUNT(*) FROM salon_bookings WHERE salonId = :salonId AND bookingDate = :date AND timeSlot = :timeSlot AND status != 'CANCELLED'")
  suspend fun countSlotBookings(salonId: String, date: String, timeSlot: String): Int

  @Query("SELECT * FROM salon_bookings WHERE customerId = :customerId ORDER BY createdAt DESC")
  suspend fun getBookingsForCustomer(customerId: String): List<SalonBookingEntity>

  @Query("SELECT COUNT(*) FROM salon_bookings WHERE salonId = :salonId")
  suspend fun countBookingsForSalon(salonId: String): Int

  // ─── OWNER NOTIFICATIONS ────────────────────────────────
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotification(notification: OwnerNotificationEntity)

  @Query("SELECT * FROM owner_notifications WHERE salonId = :salonId ORDER BY timestamp DESC")
  suspend fun getNotificationsForSalon(salonId: String): List<OwnerNotificationEntity>

  @Query("SELECT * FROM owner_notifications WHERE salonId = :salonId ORDER BY timestamp DESC")
  fun observeNotificationsForSalon(salonId: String): Flow<List<OwnerNotificationEntity>>

  @Query("UPDATE owner_notifications SET isRead = 1 WHERE notificationId = :notifId")
  suspend fun markNotificationAsRead(notifId: String)

  @Query("SELECT COUNT(*) FROM owner_notifications WHERE salonId = :salonId AND isRead = 0")
  fun observeUnreadNotificationCount(salonId: String): Flow<Int>
}
