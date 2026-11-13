package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey val customerId: String,
    val name: String,
    val mobileNumber: String,
    val normalizedPhone: String = mobileNumber,
    val passwordHash: String,
    val profilePhotoUri: String = "",
    val address: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val fullName: String
        get() = name
}

