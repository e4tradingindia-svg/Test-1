package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.data.database.SalonMateDatabase
import com.example.data.repository.CustomerRepository
import com.example.data.repository.SalonRepository
import com.example.data.session.SessionManager
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.BlackBackground
import com.example.ui.theme.SalonMateTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = SalonMateDatabase.getDatabase(applicationContext)
        val customerRepository = CustomerRepository(database.customerDao())
        val salonRepository = SalonRepository(database.salonDao())
        val sessionManager = SessionManager(applicationContext)

        lifecycleScope.launch {
            salonRepository.seedDefaultsIfNeeded()
        }

        setContent {
            SalonMateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BlackBackground
                ) {
                    AppNavigation(
                        customerRepository = customerRepository,
                        salonRepository = salonRepository,
                        sessionManager = sessionManager
                    )
                }
            }
        }
    }
}
