package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.local.SalonMateDatabase
import com.example.data.model.CustomerEntity
import com.example.data.model.SalonEntity
import com.example.data.repository.CustomerRepository
import com.example.data.repository.SalonRepository
import com.example.data.session.CustomerSessionManager
import com.example.ui.components.SalonMateLogoBadge
import com.example.ui.customer.CustomerAuthScreen
import com.example.ui.customer.CustomerPortalScreen
import com.example.ui.customer.CustomerViewSalonScreen
import com.example.ui.customer.EditCustomerProfileScreen
import com.example.ui.owner.EditSalonBusinessProfileScreen
import com.example.ui.owner.OwnerBusinessProfile
import com.example.ui.owner.SalonOwnerAuthScreen
import com.example.ui.owner.SalonOwnerDashboardScreen
import com.example.ui.theme.LuxuryBlack
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.welcome.WelcomeScreen
import kotlinx.coroutines.launch

sealed interface SalonMateScreen {
  object Loading : SalonMateScreen
  object Welcome : SalonMateScreen
  object CustomerAuth : SalonMateScreen
  data class CustomerPortal(val customer: CustomerEntity) : SalonMateScreen
  data class EditCustomerProfile(val customer: CustomerEntity) : SalonMateScreen
  object OwnerAuth : SalonMateScreen
  data class OwnerDashboard(val profile: OwnerBusinessProfile) : SalonMateScreen
  data class EditSalonProfile(val profile: OwnerBusinessProfile) : SalonMateScreen
  data class ViewSalon(val salon: SalonEntity, val returnCustomer: CustomerEntity) : SalonMateScreen
}

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = SalonMateDatabase.getDatabase(this)
    val customerRepository = CustomerRepository(database.customerDao())
    val salonRepository = SalonRepository(database.salonDao())
    val sessionManager = CustomerSessionManager(this)

    setContent {
      MyApplicationTheme {
        Scaffold(
          modifier = Modifier.fillMaxSize(),
          containerColor = LuxuryBlack
        ) { _ ->
          SalonMateApp(
            repository = customerRepository,
            salonRepository = salonRepository,
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
  salonRepository: SalonRepository,
  sessionManager: CustomerSessionManager,
  modifier: Modifier = Modifier
) {
  val coroutineScope = rememberCoroutineScope()
  val salons by salonRepository.observeAllSalons().collectAsState(initial = emptyList())
  val hasPersistedSession = remember { sessionManager.hasActiveSession() }
  var currentScreen by remember {
    mutableStateOf<SalonMateScreen>(
      if (hasPersistedSession) SalonMateScreen.Loading else SalonMateScreen.Welcome
    )
  }

  // Check and restore persisted session on app launch before showing opening screen
  LaunchedEffect(Unit) {
    salonRepository.ensureInitialSalons()
    val persistedId = sessionManager.getPersistedCustomerId()
    if (!persistedId.isNullOrEmpty()) {
      val customer = repository.getCustomerById(persistedId)
      if (customer != null) {
        currentScreen = SalonMateScreen.CustomerPortal(customer)
      } else {
        sessionManager.clearSession()
        currentScreen = SalonMateScreen.Welcome
      }
    } else {
      currentScreen = SalonMateScreen.Welcome
    }
  }

  when (val screen = currentScreen) {
    is SalonMateScreen.Loading -> {
      Box(
        modifier = modifier
          .fillMaxSize()
          .background(LuxuryBlack),
        contentAlignment = Alignment.Center
      ) {
        SalonMateLogoBadge(modifier = Modifier.size(92.dp))
      }
    }

    is SalonMateScreen.Welcome -> {
      WelcomeScreen(
        modifier = modifier,
        onCustomerClick = {
          currentScreen = SalonMateScreen.CustomerAuth
        },
        onOwnerClick = {
          currentScreen = SalonMateScreen.OwnerAuth
        }
      )
    }

    is SalonMateScreen.CustomerAuth -> {
      CustomerAuthScreen(
        repository = repository,
        modifier = modifier,
        onAuthSuccess = { customer ->
          sessionManager.saveSession(customer.customerId)
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
        salons = salons,
        onSalonSelected = { selectedSalon ->
          currentScreen = SalonMateScreen.ViewSalon(salon = selectedSalon, returnCustomer = screen.customer)
        },
        modifier = modifier,
        onEditProfileClick = {
          currentScreen = SalonMateScreen.EditCustomerProfile(screen.customer)
        },
        onLogout = {
          sessionManager.clearSession()
          currentScreen = SalonMateScreen.Welcome
        }
      )
    }

    is SalonMateScreen.ViewSalon -> {
      CustomerViewSalonScreen(
        salon = screen.salon,
        onBack = {
          currentScreen = SalonMateScreen.CustomerPortal(screen.returnCustomer)
        },
        modifier = modifier
      )
    }

    is SalonMateScreen.EditCustomerProfile -> {
      EditCustomerProfileScreen(
        customer = screen.customer,
        repository = repository,
        modifier = modifier,
        onProfileUpdated = { updatedCustomer ->
          currentScreen = SalonMateScreen.CustomerPortal(updatedCustomer)
        },
        onBack = {
          currentScreen = SalonMateScreen.CustomerPortal(screen.customer)
        }
      )
    }

    is SalonMateScreen.OwnerAuth -> {
      SalonOwnerAuthScreen(
        modifier = modifier,
        onAuthSuccess = { authProfile ->
          coroutineScope.launch {
            val existing = salonRepository.getSalonById(authProfile.salonId)
            val effectiveProfile = if (existing != null) {
              authProfile.copy(
                bannerUri = existing.bannerUri,
                address = existing.address,
                mobileNumber = existing.mobileNumber,
                latitude = existing.latitude,
                longitude = existing.longitude,
                isActive = existing.isActive
              )
            } else {
              salonRepository.insertOrUpdateSalon(
                SalonEntity(
                  salonId = authProfile.salonId,
                  salonName = authProfile.salonName,
                  ownerName = authProfile.ownerName,
                  mobileNumber = authProfile.mobileNumber,
                  businessType = authProfile.businessType,
                  bannerUri = authProfile.bannerUri,
                  address = authProfile.address,
                  latitude = authProfile.latitude,
                  longitude = authProfile.longitude,
                  isActive = authProfile.isActive
                )
              )
              authProfile
            }
            currentScreen = SalonMateScreen.OwnerDashboard(effectiveProfile)
          }
        },
        onBackClick = {
          currentScreen = SalonMateScreen.Welcome
        }
      )
    }

    is SalonMateScreen.OwnerDashboard -> {
      SalonOwnerDashboardScreen(
        profile = screen.profile,
        salonRepository = salonRepository,
        modifier = modifier,
        onEditProfileClick = {
          currentScreen = SalonMateScreen.EditSalonProfile(screen.profile)
        },
        onLogout = {
          currentScreen = SalonMateScreen.Welcome
        }
      )
    }

    is SalonMateScreen.EditSalonProfile -> {
      EditSalonBusinessProfileScreen(
        profile = screen.profile,
        salonRepository = salonRepository,
        modifier = modifier,
        onProfileUpdated = { updatedProfile ->
          currentScreen = SalonMateScreen.OwnerDashboard(updatedProfile)
        },
        onBack = {
          currentScreen = SalonMateScreen.OwnerDashboard(screen.profile)
        }
      )
    }
  }
}
