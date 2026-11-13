package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "salons")
data class SalonEntity(
    @PrimaryKey val salonId: String,
    val salonName: String,
    val ownerName: String,
    val mobileNumber: String,
    val passwordHash: String = "",
    val businessType: String = "Unisex Salon",
    val bannerUri: String = "",
    val interiorPhotoUris: String = "", // Comma-separated URIs of up to 4 interior photos
    val address: String = "Main High Street, Central Plaza",
    val latitude: Double = 28.6139,
    val longitude: Double = 77.2090,
    val rating: Double = 4.8,
    val reviewCount: Int = 124,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getInteriorPhotoList(): List<String> {
        if (interiorPhotoUris.isBlank()) return emptyList()
        return interiorPhotoUris.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .take(4)
    }
}
