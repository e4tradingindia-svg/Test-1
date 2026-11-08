package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Customer Entity storing registered SalonMate customer profile and credentials.
 * The normalized phone number has a UNIQUE index to prevent duplicate accounts.
 */
@Entity(
  tableName = "customers",
  indices = [Index(value = ["normalizedPhone"], unique = true)]
)
data class CustomerEntity(
  @PrimaryKey
  val customerId: String,
  val fullName: String,
  val normalizedPhone: String,
  val passwordHash: String,
  val passwordSalt: String,
  val profilePhotoUri: String? = null,
  val address: String? = null,
  val createdAt: Long = System.currentTimeMillis()
)
