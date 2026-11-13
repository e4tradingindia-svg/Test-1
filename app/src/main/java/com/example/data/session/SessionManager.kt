package com.example.data.session

import android.content.Context
import android.content.SharedPreferences

data class CustomerSession(
    val customerId: String,
    val name: String,
    val mobileNumber: String
)

data class OwnerSession(
    val salonId: String,
    val salonName: String,
    val mobileNumber: String
)

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("salonmate_session_prefs", Context.MODE_PRIVATE)

    fun saveCustomerSession(customerId: String, name: String, mobileNumber: String) {
        prefs.edit()
            .putString("customer_id", customerId)
            .putString("customer_name", name)
            .putString("customer_mobile", mobileNumber)
            .putString("active_role", "CUSTOMER")
            .apply()
    }

    fun getCustomerSession(): CustomerSession? {
        val id = prefs.getString("customer_id", null) ?: return null
        val name = prefs.getString("customer_name", "") ?: ""
        val mobile = prefs.getString("customer_mobile", "") ?: ""
        return CustomerSession(id, name, mobile)
    }

    fun clearCustomerSession() {
        prefs.edit()
            .remove("customer_id")
            .remove("customer_name")
            .remove("customer_mobile")
            .remove("active_role")
            .apply()
    }

    fun saveOwnerSession(salonId: String, salonName: String, mobileNumber: String) {
        prefs.edit()
            .putString("salon_id", salonId)
            .putString("salon_name", salonName)
            .putString("salon_mobile", mobileNumber)
            .putString("active_role", "OWNER")
            .apply()
    }

    fun getOwnerSession(): OwnerSession? {
        val id = prefs.getString("salon_id", null) ?: return null
        val name = prefs.getString("salon_name", "") ?: ""
        val mobile = prefs.getString("salon_mobile", "") ?: ""
        return OwnerSession(id, name, mobile)
    }

    fun clearOwnerSession() {
        prefs.edit()
            .remove("salon_id")
            .remove("salon_name")
            .remove("salon_mobile")
            .remove("active_role")
            .apply()
    }

    fun getActiveRole(): String? {
        return prefs.getString("active_role", null)
    }
}
