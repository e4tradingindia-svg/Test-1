package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Salon Entity storing registered salon/parlour business details, banner, address, contact, and GPS.
 */
@Entity(tableName = "salons")
data class SalonEntity(
  @PrimaryKey
  val salonId: String,
  val salonName: String,
  val ownerName: String,
  val mobileNumber: String,
  val businessType: String,
  val bannerUri: String? = null,
  val address: String? = null,
  val latitude: Double? = null,
  val longitude: Double? = null,
  val rating: Double = 4.9,
  val reviewCount: Int = 128,
  val isActive: Boolean = true,
  val createdAt: Long = System.currentTimeMillis()
)
