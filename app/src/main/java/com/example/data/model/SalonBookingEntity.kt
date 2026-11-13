package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "salon_bookings")
data class SalonBookingEntity(
    @PrimaryKey val bookingId: String,
    val salonId: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val serviceIds: String,
    val serviceNames: String,
    val bookingDate: String,
    val timeSlot: String,
    val totalAmount: Double,
    val status: String = "CONFIRMED",
    val createdAt: Long = System.currentTimeMillis()
)
