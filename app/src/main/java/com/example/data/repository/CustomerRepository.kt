package com.example.data.repository

import com.example.data.dao.CustomerDao
import com.example.data.model.CustomerEntity
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest

class CustomerRepository(private val customerDao: CustomerDao) {

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun registerCustomer(name: String, mobileNumber: String, password: String): Result<CustomerEntity> {
        val trimmedMobile = mobileNumber.trim()
        val trimmedName = name.trim()
        if (trimmedMobile.length < 10) {
            return Result.failure(IllegalArgumentException("Please enter a valid 10-digit mobile number"))
        }
        if (trimmedName.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter your name"))
        }
        if (password.length < 4) {
            return Result.failure(IllegalArgumentException("Password must be at least 4 characters"))
        }

        val existing = customerDao.getCustomerByMobile(trimmedMobile)
        if (existing != null) {
            return Result.failure(IllegalStateException("A customer with this mobile number is already registered"))
        }

        val customerId = "CUST-" + trimmedMobile.takeLast(6)
        val customer = CustomerEntity(
            customerId = customerId,
            name = trimmedName,
            mobileNumber = trimmedMobile,
            normalizedPhone = trimmedMobile,
            passwordHash = hashPassword(password)
        )
        customerDao.insertCustomer(customer)
        return Result.success(customer)
    }

    suspend fun loginCustomer(mobileNumber: String, password: String): Result<CustomerEntity> {
        val trimmedMobile = mobileNumber.trim()
        val customer = customerDao.getCustomerByMobile(trimmedMobile)
            ?: return Result.failure(IllegalArgumentException("No customer account found with this mobile number"))

        val hashed = hashPassword(password)
        if (customer.passwordHash != hashed) {
            return Result.failure(IllegalArgumentException("Incorrect password"))
        }
        return Result.success(customer)
    }

    suspend fun getCustomer(customerId: String): CustomerEntity? {
        return customerDao.getCustomerById(customerId)
    }

    fun observeCustomer(customerId: String): Flow<CustomerEntity?> {
        return customerDao.observeCustomerById(customerId)
    }

    suspend fun updateProfile(customerId: String, photoUri: String, address: String) {
        customerDao.updateProfile(customerId, photoUri, address)
    }

    suspend fun authenticateCustomer(rawPhone: String, password: String): Result<CustomerEntity> {
        return loginCustomer(rawPhone.trim(), password)
    }

    suspend fun registerCustomer(
        fullName: String,
        rawPhone: String,
        password: String,
        confirmPassword: String
    ): Result<CustomerEntity> {
        if (password != confirmPassword) {
            return Result.failure(IllegalArgumentException("Passwords do not match"))
        }
        return registerCustomer(
            name = fullName,
            mobileNumber = rawPhone,
            password = password
        )
    }

    suspend fun updateCustomerProfile(
        customerId: String,
        photoUri: String?,
        address: String?
    ): Result<CustomerEntity> {
        customerDao.updateProfile(
            customerId = customerId,
            photoUri = photoUri.orEmpty(),
            address = address.orEmpty().trim()
        )
        val updated = customerDao.getCustomerById(customerId)
            ?: return Result.failure(IllegalStateException("Customer account not found"))
        return Result.success(updated)
    }
}

