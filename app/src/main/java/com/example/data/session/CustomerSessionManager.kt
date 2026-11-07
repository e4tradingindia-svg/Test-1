package com.example.data.session

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages customer authentication session and persistence.
 * Authenticated customer sessions persist automatically across app restarts.
 * Explicit Sign Out clears the session without deleting account data.
 */
class CustomerSessionManager(context: Context) {

  private val prefs: SharedPreferences = context.getSharedPreferences(
    PREFS_NAME,
    Context.MODE_PRIVATE
  )

  companion object {
    private const val PREFS_NAME = "salonmate_customer_session"
    private const val KEY_CUSTOMER_ID = "persisted_customer_id"
  }

  /**
   * Saves the authenticated customer session automatically.
   */
  fun saveSession(customerId: String) {
    prefs.edit().apply {
      putString(KEY_CUSTOMER_ID, customerId)
      apply()
    }
  }

  /**
   * Overload for backward compatibility if called with boolean.
   */
  fun saveSession(customerId: String, @Suppress("UNUSED_PARAMETER") rememberMe: Boolean) {
    saveSession(customerId)
  }

  /**
   * Returns the persisted customer ID if an active session exists, or null.
   */
  fun getPersistedCustomerId(): String? {
    return prefs.getString(KEY_CUSTOMER_ID, null)
  }

  /**
   * Returns whether a session is active.
   */
  fun hasActiveSession(): Boolean {
    return !getPersistedCustomerId().isNullOrEmpty()
  }

  /**
   * Clears the active session upon explicit logout.
   * Does NOT delete the customer account in the database.
   */
  fun clearSession() {
    prefs.edit().apply {
      remove(KEY_CUSTOMER_ID)
      apply()
    }
  }
}
