package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.repository.CustomerRepository
import com.example.data.repository.SalonRepository
import com.example.data.session.SessionManager
import com.example.ui.screens.customer.CustomerEditProfileScreen
import com.example.ui.screens.customer.CustomerLoginScreen
import com.example.ui.screens.customer.CustomerPortalScreen
import com.example.ui.screens.customer.CustomerRegisterScreen
import com.example.ui.screens.customer.CustomerViewSalonScreen
import com.example.ui.screens.owner.EditSalonBusinessProfileScreen
import com.example.ui.screens.owner.SalonOwnerDashboardScreen
import com.example.ui.screens.owner.SalonOwnerLoginScreen
import com.example.ui.screens.owner.SalonOwnerRegisterScreen
import com.example.ui.screens.welcome.RoleSelectionScreen

object Routes {
    const val ROLE_SELECTION = "role_selection"
    const val CUSTOMER_LOGIN = "customer_login"
    const val CUSTOMER_REGISTER = "customer_register"
    const val CUSTOMER_PORTAL = "customer_portal/{customerId}"
    const val CUSTOMER_EDIT_PROFILE = "customer_edit_profile/{customerId}"
    const val CUSTOMER_VIEW_SALON = "customer_view_salon/{salonId}/{customerId}"
    const val OWNER_LOGIN = "owner_login"
    const val OWNER_REGISTER = "owner_register"
    const val OWNER_DASHBOARD = "owner_dashboard/{salonId}"
    const val OWNER_EDIT_PROFILE = "owner_edit_profile/{salonId}"

    fun customerPortal(customerId: String) = "customer_portal/$customerId"
    fun customerEditProfile(customerId: String) = "customer_edit_profile/$customerId"
    fun customerViewSalon(salonId: String, customerId: String) = "customer_view_salon/$salonId/$customerId"
    fun ownerDashboard(salonId: String) = "owner_dashboard/$salonId"
    fun ownerEditProfile(salonId: String) = "owner_edit_profile/$salonId"
}

@Composable
fun AppNavigation(
    customerRepository: CustomerRepository,
    salonRepository: SalonRepository,
    sessionManager: SessionManager
) {
    val navController = rememberNavController()

    // Determine starting route from persisted session
    val startDestination = when (sessionManager.getActiveRole()) {
        "CUSTOMER" -> {
            val session = sessionManager.getCustomerSession()
            if (session != null) Routes.customerPortal(session.customerId) else Routes.ROLE_SELECTION
        }
        "OWNER" -> {
            val session = sessionManager.getOwnerSession()
            if (session != null) Routes.ownerDashboard(session.salonId) else Routes.ROLE_SELECTION
        }
        else -> Routes.ROLE_SELECTION
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Welcome / Role Selection
        composable(Routes.ROLE_SELECTION) {
            RoleSelectionScreen(
                onNavigateToCustomer = { navController.navigate(Routes.CUSTOMER_LOGIN) },
                onNavigateToOwner = { navController.navigate(Routes.OWNER_LOGIN) }
            )
        }

        // Customer Login
        composable(Routes.CUSTOMER_LOGIN) {
            CustomerLoginScreen(
                customerRepository = customerRepository,
                sessionManager = sessionManager,
                onLoginSuccess = { customerId ->
                    navController.navigate(Routes.customerPortal(customerId)) {
                        popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.CUSTOMER_REGISTER) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Customer Register
        composable(Routes.CUSTOMER_REGISTER) {
            CustomerRegisterScreen(
                customerRepository = customerRepository,
                sessionManager = sessionManager,
                onRegisterSuccess = { customerId ->
                    navController.navigate(Routes.customerPortal(customerId)) {
                        popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.navigate(Routes.CUSTOMER_LOGIN) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Customer Portal
        composable(
            route = Routes.CUSTOMER_PORTAL,
            arguments = listOf(navArgument("customerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
            CustomerPortalScreen(
                customerId = customerId,
                customerRepository = customerRepository,
                salonRepository = salonRepository,
                sessionManager = sessionManager,
                onNavigateToEditProfile = {
                    navController.navigate(Routes.customerEditProfile(customerId))
                },
                onNavigateToSalonDetails = { salonId ->
                    navController.navigate(Routes.customerViewSalon(salonId, customerId))
                },
                onLogout = {
                    navController.navigate(Routes.ROLE_SELECTION) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Customer Edit Profile
        composable(
            route = Routes.CUSTOMER_EDIT_PROFILE,
            arguments = listOf(navArgument("customerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
            CustomerEditProfileScreen(
                customerId = customerId,
                customerRepository = customerRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Customer View Salon Details (Booking & Gallery)
        composable(
            route = Routes.CUSTOMER_VIEW_SALON,
            arguments = listOf(
                navArgument("salonId") { type = NavType.StringType },
                navArgument("customerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val salonId = backStackEntry.arguments?.getString("salonId") ?: ""
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
            CustomerViewSalonScreen(
                salonId = salonId,
                customerId = customerId,
                salonRepository = salonRepository,
                customerRepository = customerRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Salon Owner Login
        composable(Routes.OWNER_LOGIN) {
            SalonOwnerLoginScreen(
                salonRepository = salonRepository,
                sessionManager = sessionManager,
                onLoginSuccess = { salonId ->
                    navController.navigate(Routes.ownerDashboard(salonId)) {
                        popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.OWNER_REGISTER) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Salon Owner Register
        composable(Routes.OWNER_REGISTER) {
            SalonOwnerRegisterScreen(
                salonRepository = salonRepository,
                sessionManager = sessionManager,
                onRegisterSuccess = { salonId ->
                    navController.navigate(Routes.ownerDashboard(salonId)) {
                        popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.navigate(Routes.OWNER_LOGIN) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Salon Owner Dashboard
        composable(
            route = Routes.OWNER_DASHBOARD,
            arguments = listOf(navArgument("salonId") { type = NavType.StringType })
        ) { backStackEntry ->
            val salonId = backStackEntry.arguments?.getString("salonId") ?: ""
            SalonOwnerDashboardScreen(
                salonId = salonId,
                salonRepository = salonRepository,
                sessionManager = sessionManager,
                onNavigateToEditProfile = {
                    navController.navigate(Routes.ownerEditProfile(salonId))
                },
                onLogout = {
                    navController.navigate(Routes.ROLE_SELECTION) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Salon Owner Edit Business Profile
        composable(
            route = Routes.OWNER_EDIT_PROFILE,
            arguments = listOf(navArgument("salonId") { type = NavType.StringType })
        ) { backStackEntry ->
            val salonId = backStackEntry.arguments?.getString("salonId") ?: ""
            EditSalonBusinessProfileScreen(
                salonId = salonId,
                salonRepository = salonRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
