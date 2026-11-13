package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "salon_business_hours")
data class SalonBusinessHoursEntity(
    @PrimaryKey val id: String, // salonId_dayOfWeek
    val salonId: String,
    val dayOfWeek: Int, // 1 = Monday ... 7 = Sunday
    val openTime: String = "09:00",
    val closeTime: String = "20:00",
    val isOpen: Boolean = true
)
