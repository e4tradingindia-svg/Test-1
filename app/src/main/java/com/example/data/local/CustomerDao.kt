package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.CustomerEntity

@Dao
interface CustomerDao {

  @Insert(onConflict = OnConflictStrategy.ABORT)
  suspend fun insertCustomer(customer: CustomerEntity)

  @Query("SELECT * FROM customers WHERE normalizedPhone = :phone OR mobileNumber = :phone LIMIT 1")
  suspend fun getCustomerByPhone(phone: String): CustomerEntity?

  @Query("SELECT * FROM customers WHERE customerId = :customerId LIMIT 1")
  suspend fun getCustomerById(customerId: String): CustomerEntity?

  @Query("SELECT COUNT(*) FROM customers WHERE normalizedPhone = :phone OR mobileNumber = :phone")
  suspend fun countByPhone(phone: String): Int

  @Query("UPDATE customers SET profilePhotoUri = :photoUri, address = :address WHERE customerId = :customerId")
  suspend fun updateProfile(customerId: String, photoUri: String?, address: String?)
}
