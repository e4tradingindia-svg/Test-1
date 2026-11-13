package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "salon_services")
data class SalonServiceEntity(
    @PrimaryKey val serviceId: String,
    val salonId: String,
    val serviceName: String,
    val category: String,
    val price: Double,
    val durationMinutes: Int,
    val description: String = "",
    val isAvailable: Boolean = true
) {
    val name: String
        get() = serviceName
}

