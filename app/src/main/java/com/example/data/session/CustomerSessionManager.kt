package com.example.data.session

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages customer authentication session and persistence.
 * If Remember Me is enabled, the session survives app restarts.
 * Logging out clears the session without deleting account data.
 */
class CustomerSessionManager(context: Context) {

  private val prefs: SharedPreferences = context.getSharedPreferences(
    PREFS_NAME,
    Context.MODE_PRIVATE
  )

  companion object {
    private const val PREFS_NAME = "salonmate_customer_session"
    private const val KEY_CUSTOMER_ID = "persisted_customer_id"
    private const val KEY_REMEMBER_ME = "remember_me"
  }

  /**
   * Saves the authenticated customer session.
   * If rememberMe is false, the customerId is not saved to persistent storage.
   */
  fun saveSession(customerId: String, rememberMe: Boolean) {
    prefs.edit().apply {
      putBoolean(KEY_REMEMBER_ME, rememberMe)
      if (rememberMe) {
        putString(KEY_CUSTOMER_ID, customerId)
      } else {
        remove(KEY_CUSTOMER_ID)
      }
      apply()
    }
  }

  /**
   * Returns the persisted customer ID if Remember Me was selected, or null.
   */
  fun getPersistedCustomerId(): String? {
    val isRemembered = prefs.getBoolean(KEY_REMEMBER_ME, false)
    return if (isRemembered) {
      prefs.getString(KEY_CUSTOMER_ID, null)
    } else {
      null
    }
  }

  /**
   * Returns whether Remember Me is checked by default.
   */
  fun isRememberMeEnabled(): Boolean {
    return prefs.getBoolean(KEY_REMEMBER_ME, true)
  }

  /**
   * Clears the active session upon logout.
   * Does NOT delete the customer account in the database.
   */
  fun clearSession() {
    prefs.edit().apply {
      remove(KEY_CUSTOMER_ID)
      remove(KEY_REMEMBER_ME)
      apply()
    }
  }
}
