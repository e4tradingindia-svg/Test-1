package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SalonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SalonDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateSalon(salon: SalonEntity)

  @Update
  suspend fun updateSalon(salon: SalonEntity)

  @Query("SELECT * FROM salons WHERE salonId = :salonId LIMIT 1")
  suspend fun getSalonById(salonId: String): SalonEntity?

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
}
