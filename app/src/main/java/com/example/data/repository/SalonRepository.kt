package com.example.data.repository

import com.example.data.local.SalonDao
import com.example.data.model.SalonEntity
import kotlinx.coroutines.flow.Flow

class SalonRepository(private val salonDao: SalonDao) {

  /**
   * Initializes or returns default sample salons if the database is empty,
   * ensuring that customers immediately see realistic salons in Listings.
   */
  suspend fun ensureInitialSalons() {
    if (salonDao.countById("SALON-ROYAL-01") == 0) {
      val defaultSalons = listOf(
        SalonEntity(
          salonId = "SALON-ROYAL-01",
          salonName = "Royal Crown Luxury Unisex Salon",
          ownerName = "Marco Silva",
          mobileNumber = "+1 (555) 234-5678",
          businessType = "Unisex Salon & Spa",
          bannerUri = null,
          address = "450 Grand Avenue, Suite 102, Metro City",
          latitude = 37.7749,
          longitude = -122.4194,
          rating = 4.9,
          reviewCount = 184
        ),
        SalonEntity(
          salonId = "SALON-GLAM-02",
          salonName = "Glamour Haven Beauty Parlour",
          ownerName = "Sophia Martinez",
          mobileNumber = "+1 (555) 876-5432",
          businessType = "Women Beauty Parlour",
          bannerUri = null,
          address = "128 Rose Boulevard, Downtown",
          latitude = 37.7833,
          longitude = -122.4167,
          rating = 4.8,
          reviewCount = 142
        ),
        SalonEntity(
          salonId = "SALON-BARBER-03",
          salonName = "Gentlemen's Classic Barber Lounge",
          ownerName = "James Wilson",
          mobileNumber = "+1 (555) 345-6789",
          businessType = "Men Salon",
          bannerUri = null,
          address = "88 King's Road, Financial District",
          latitude = 37.7892,
          longitude = -122.4014,
          rating = 4.9,
          reviewCount = 210
        )
      )
      defaultSalons.forEach { salonDao.insertOrUpdateSalon(it) }
    }
  }

  suspend fun getSalonById(salonId: String): SalonEntity? {
    return salonDao.getSalonById(salonId)
  }

  fun observeSalonById(salonId: String): Flow<SalonEntity?> {
    return salonDao.observeSalonById(salonId)
  }

  suspend fun getAllSalons(): List<SalonEntity> {
    ensureInitialSalons()
    return salonDao.getAllSalons()
  }

  fun observeAllSalons(): Flow<List<SalonEntity>> {
    return salonDao.observeAllSalons()
  }

  suspend fun saveOrUpdateSalon(salon: SalonEntity) {
    salonDao.insertOrUpdateSalon(salon)
  }

  suspend fun insertOrUpdateSalon(salon: SalonEntity) {
    salonDao.insertOrUpdateSalon(salon)
  }

  /**
   * Updates salon banner, address, contact, and GPS location.
   */
  suspend fun updateSalonBusinessProfile(
    salonId: String,
    bannerUri: String?,
    address: String?,
    mobileNumber: String,
    latitude: Double?,
    longitude: Double?
  ): Result<SalonEntity> {
    var existing = salonDao.getSalonById(salonId)
    if (existing == null) {
      existing = SalonEntity(
        salonId = salonId,
        salonName = "Elite Salon & Spa",
        ownerName = "Salon Partner",
        mobileNumber = mobileNumber,
        businessType = "Unisex Salon & Spa"
      )
      salonDao.insertOrUpdateSalon(existing)
    }

    val updated = existing.copy(
      bannerUri = bannerUri,
      address = address?.trim()?.ifEmpty { null },
      mobileNumber = mobileNumber.trim(),
      latitude = latitude,
      longitude = longitude
    )
    salonDao.insertOrUpdateSalon(updated)
    return Result.success(updated)
  }

  /**
   * Updates whether the salon is ACTIVE (accepting bookings/orders)
   * or DEACTIVATED (offline / unavailable to customers).
   */
  suspend fun updateSalonActiveStatus(salonId: String, isActive: Boolean): Result<Boolean> {
    val existing = salonDao.getSalonById(salonId)
    if (existing == null) {
      val initial = SalonEntity(
        salonId = salonId,
        salonName = "Elite Salon & Spa",
        ownerName = "Salon Partner",
        mobileNumber = "+1 (555) 234-5678",
        businessType = "Unisex Salon & Spa",
        isActive = isActive
      )
      salonDao.insertOrUpdateSalon(initial)
    } else {
      salonDao.updateSalonActiveStatus(salonId, isActive)
    }
    return Result.success(isActive)
  }
}
