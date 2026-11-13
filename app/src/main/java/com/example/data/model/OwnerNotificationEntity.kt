package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "owner_notifications")
data class OwnerNotificationEntity(
    @PrimaryKey val notificationId: String,
    val salonId: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
