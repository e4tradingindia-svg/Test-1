package com.example.data.repository

import com.example.data.local.CustomerDao
import com.example.data.model.CustomerEntity
import com.example.data.security.PasswordHasher
import java.util.UUID

class CustomerRepository(private val customerDao: CustomerDao) {

  /**
   * Normalizes phone number to digits only (e.g., strips spaces, dashes, parentheses).
   */
  fun normalizePhone(rawPhone: String): String {
    val digits = rawPhone.filter { it.isDigit() }
    return if (digits.length == 11 && digits.startsWith("1")) {
      digits.substring(1)
    } else {
      digits
    }
  }

  /**
   * Validates mobile number format (must be 10-15 digits).
   */
  fun isValidPhone(normalizedPhone: String): Boolean {
    return normalizedPhone.length in 10..15
  }

  /**
   * Generates a unique, persistent Customer ID (e.g. CUST-A1B2C3D4).
   */
  private fun generateCustomerId(): String {
    val randomPart = UUID.randomUUID().toString().replace("-", "").take(8).uppercase()
    return "CUST-$randomPart"
  }

  /**
   * Registers a new customer account.
   * Prevents duplicate accounts for the same normalized mobile number.
   */
  suspend fun registerCustomer(
    fullName: String,
    rawPhone: String,
    password: String,
    confirmPassword: String
  ): Result<CustomerEntity> {
    val trimmedName = fullName.trim()
    if (trimmedName.isEmpty()) {
      return Result.failure(IllegalArgumentException("Please enter your full name."))
    }

    val normalizedPhone = normalizePhone(rawPhone)
    if (!isValidPhone(normalizedPhone)) {
      return Result.failure(IllegalArgumentException("Please enter a valid mobile number (at least 10 digits)."))
    }

    if (password.length < 6) {
      return Result.failure(IllegalArgumentException("Password must be at least 6 characters."))
    }

    if (password != confirmPassword) {
      return Result.failure(IllegalArgumentException("Passwords do not match."))
    }

    // Check for duplicate account
    val existing = customerDao.getCustomerByPhone(normalizedPhone)
    if (existing != null) {
      return Result.failure(IllegalStateException("An account with this mobile number already exists. Please Sign In."))
    }

    val salt = PasswordHasher.generateSalt()
    val hash = PasswordHasher.hashPassword(password, salt)
    val customerId = generateCustomerId()

    val newCustomer = CustomerEntity(
      customerId = customerId,
      fullName = trimmedName,
      normalizedPhone = normalizedPhone,
      passwordHash = hash,
      passwordSalt = salt
    )

    try {
      customerDao.insertCustomer(newCustomer)
      return Result.success(newCustomer)
    } catch (e: Exception) {
      return Result.failure(IllegalStateException("Failed to create account. An account with this mobile number may already exist."))
    }
  }

  /**
   * Authenticates an existing customer.
   * Compares the password hash against the stored salted hash.
   */
  suspend fun authenticateCustomer(
    rawPhone: String,
    password: String
  ): Result<CustomerEntity> {
    val normalizedPhone = normalizePhone(rawPhone)
    if (normalizedPhone.isEmpty()) {
      return Result.failure(IllegalArgumentException("Please enter your mobile number."))
    }

    if (password.isEmpty()) {
      return Result.failure(IllegalArgumentException("Please enter your password."))
    }

    val customer = customerDao.getCustomerByPhone(normalizedPhone)
      ?: return Result.failure(IllegalArgumentException("Invalid mobile number or password."))

    val isPasswordValid = PasswordHasher.verifyPassword(
      plainPassword = password,
      salt = customer.passwordSalt,
      expectedHash = customer.passwordHash
    )

    return if (isPasswordValid) {
      Result.success(customer)
    } else {
      Result.failure(IllegalArgumentException("Invalid mobile number or password."))
    }
  }

  /**
   * Retrieves a customer by their unique persistent ID.
   */
  suspend fun getCustomerById(customerId: String): CustomerEntity? {
    return customerDao.getCustomerById(customerId)
  }

  /**
   * Updates customer profile photo and address.
   * Keeps customerId, fullName, normalizedPhone, and credentials unchanged.
   */
  suspend fun updateCustomerProfile(
    customerId: String,
    photoUri: String?,
    address: String?
  ): Result<CustomerEntity> {
    val existing = customerDao.getCustomerById(customerId)
      ?: return Result.failure(IllegalArgumentException("Customer account not found."))

    val trimmedAddress = address?.trim()?.ifEmpty { null }
    customerDao.updateProfile(customerId, photoUri, trimmedAddress)

    val updated = customerDao.getCustomerById(customerId)
      ?: return Result.failure(IllegalStateException("Failed to reload updated customer profile."))

    return Result.success(updated)
  }
}
