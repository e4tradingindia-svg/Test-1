package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "salon_staff")
data class SalonStaffEntity(
    @PrimaryKey val staffId: String,
    val salonId: String,
    val name: String,
    val role: String,
    val isAvailable: Boolean = true
)
