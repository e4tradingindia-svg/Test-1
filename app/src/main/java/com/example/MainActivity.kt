package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.local.SalonMateDatabase
import com.example.data.model.CustomerEntity
import com.example.data.repository.CustomerRepository
import com.example.data.session.CustomerSessionManager
import com.example.ui.customer.CustomerAuthScreen
import com.example.ui.customer.CustomerPortalScreen
import com.example.ui.theme.LuxuryBlack
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.welcome.WelcomeScreen

sealed interface SalonMateScreen {
  object Welcome : SalonMateScreen
  object CustomerAuth : SalonMateScreen
  data class CustomerPortal(val customer: CustomerEntity) : SalonMateScreen
}

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = SalonMateDatabase.getDatabase(this)
    val customerRepository = CustomerRepository(database.customerDao())
    val sessionManager = CustomerSessionManager(this)

    setContent {
      MyApplicationTheme {
        Scaffold(
          modifier = Modifier.fillMaxSize(),
          containerColor = LuxuryBlack
        ) { _ ->
          SalonMateApp(
            repository = customerRepository,
            sessionManager = sessionManager
          )
        }
      }
    }
  }
}

@Composable
fun SalonMateApp(
  repository: CustomerRepository,
  sessionManager: CustomerSessionManager,
  modifier: Modifier = Modifier
) {
  var currentScreen by remember { mutableStateOf<SalonMateScreen>(SalonMateScreen.Welcome) }

  // Check persisted session on app launch
  LaunchedEffect(Unit) {
    val persistedId = sessionManager.getPersistedCustomerId()
    if (!persistedId.isNullOrEmpty()) {
      val customer = repository.getCustomerById(persistedId)
      if (customer != null) {
        currentScreen = SalonMateScreen.CustomerPortal(customer)
      } else {
        sessionManager.clearSession()
      }
    }
  }

  when (val screen = currentScreen) {
    is SalonMateScreen.Welcome -> {
      WelcomeScreen(
        modifier = modifier,
        onCustomerClick = {
          currentScreen = SalonMateScreen.CustomerAuth
        }
      )
    }

    is SalonMateScreen.CustomerAuth -> {
      CustomerAuthScreen(
        repository = repository,
        modifier = modifier,
        initialRememberMe = sessionManager.isRememberMeEnabled(),
        onAuthSuccess = { customer, rememberMe ->
          sessionManager.saveSession(customer.customerId, rememberMe)
          currentScreen = SalonMateScreen.CustomerPortal(customer)
        },
        onBackClick = {
          currentScreen = SalonMateScreen.Welcome
        }
      )
    }

    is SalonMateScreen.CustomerPortal -> {
      CustomerPortalScreen(
        customer = screen.customer,
        modifier = modifier,
        onLogout = {
          sessionManager.clearSession()
          currentScreen = SalonMateScreen.Welcome
        }
      )
    }
  }
}
