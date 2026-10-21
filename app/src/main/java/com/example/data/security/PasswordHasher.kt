package com.example.data.security

import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Secure password hashing utility using SHA-256 with cryptographically strong salt.
 * Ensures passwords are never stored or compared in plain text.
 */
object PasswordHasher {

  private val secureRandom = SecureRandom()

  /**
   * Generates a 16-byte random salt encoded as a hexadecimal string.
   */
  fun generateSalt(): String {
    val salt = ByteArray(16)
    secureRandom.nextBytes(salt)
    return salt.joinToString("") { "%02x".format(it) }
  }

  /**
   * Hashes a plain password with a given salt using SHA-256 with multiple rounds.
   */
  fun hashPassword(password: String, salt: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    var combined = "$salt:$password".toByteArray(Charsets.UTF_8)
    repeat(1000) {
      combined = digest.digest(combined)
    }
    return combined.joinToString("") { "%02x".format(it) }
  }

  /**
   * Verifies if a plain password matches the stored hash when combined with the salt.
   */
  fun verifyPassword(plainPassword: String, salt: String, expectedHash: String): Boolean {
    val computedHash = hashPassword(plainPassword, salt)
    return computedHash.equals(expectedHash, ignoreCase = true)
  }
}
