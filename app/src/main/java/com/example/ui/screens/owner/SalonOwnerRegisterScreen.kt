package com.example.ui.screens.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SalonRepository
import com.example.data.session.SessionManager
import com.example.ui.theme.BlackBackground
import com.example.ui.theme.CinzelFamily
import com.example.ui.theme.DeactivatedRed
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LuxuryCardBg
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalonOwnerRegisterScreen(
    salonRepository: SalonRepository,
    sessionManager: SessionManager,
    onRegisterSuccess: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var salonName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var businessType by remember { mutableStateOf("Unisex Salon") }
    var categoryExpanded by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val businessTypeOptions = listOf("Unisex Salon", "Men's Salon", "Women's Beauty Parlour")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Register Business",
                        fontFamily = CinzelFamily,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("btn_owner_register_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GoldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackBackground)
            )
        },
        containerColor = BlackBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Join SalonMate",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Register your salon or parlour to manage bookings and showcase gallery",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
            )

            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(DeactivatedRed.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .border(1.dp, DeactivatedRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = DeactivatedRed,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            OutlinedTextField(
                value = salonName,
                onValueChange = {
                    salonName = it
                    errorMessage = null
                },
                label = { Text("Salon / Parlour Business Name") },
                placeholder = { Text("e.g. Maa Salon, Glamour Studio") },
                leadingIcon = {
                    Icon(Icons.Default.Business, contentDescription = null, tint = GoldPrimary)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("owner_register_salon_name_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LuxuryCardBg,
                    unfocusedContainerColor = LuxuryCardBg,
                    focusedIndicatorColor = GoldPrimary,
                    unfocusedIndicatorColor = GoldBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = GoldPrimary,
                    unfocusedLabelColor = TextSecondary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = ownerName,
                onValueChange = {
                    ownerName = it
                    errorMessage = null
                },
                label = { Text("Owner Full Name") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("owner_register_owner_name_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LuxuryCardBg,
                    unfocusedContainerColor = LuxuryCardBg,
                    focusedIndicatorColor = GoldPrimary,
                    unfocusedIndicatorColor = GoldBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = GoldPrimary,
                    unfocusedLabelColor = TextSecondary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = mobileNumber,
                onValueChange = {
                    if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                        mobileNumber = it
                        errorMessage = null
                    }
                },
                label = { Text("Owner Mobile Number (10 Digits)") },
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = GoldPrimary)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("owner_register_mobile_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LuxuryCardBg,
                    unfocusedContainerColor = LuxuryCardBg,
                    focusedIndicatorColor = GoldPrimary,
                    unfocusedIndicatorColor = GoldBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = GoldPrimary,
                    unfocusedLabelColor = TextSecondary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Business Category Dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = businessType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Business Category") },
                    leadingIcon = {
                        Icon(Icons.Default.Category, contentDescription = null, tint = GoldPrimary)
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .testTag("dropdown_business_category"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = LuxuryCardBg,
                        unfocusedContainerColor = LuxuryCardBg,
                        focusedIndicatorColor = GoldPrimary,
                        unfocusedIndicatorColor = GoldBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                    modifier = Modifier.background(LuxuryCardBg)
                ) {
                    businessTypeOptions.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt, color = TextPrimary) },
                            onClick = {
                                businessType = opt
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                label = { Text("Create Password (min 4 characters)") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = GoldPrimary)
                },
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier.testTag("btn_owner_register_toggle_password")
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = if (passwordVisible) GoldPrimary else TextSecondary
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("owner_register_password_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LuxuryCardBg,
                    unfocusedContainerColor = LuxuryCardBg,
                    focusedIndicatorColor = GoldPrimary,
                    unfocusedIndicatorColor = GoldBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = GoldPrimary,
                    unfocusedLabelColor = TextSecondary
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    if (salonName.isBlank()) {
                        errorMessage = "Please enter your salon or parlour name"
                        return@Button
                    }
                    if (ownerName.isBlank()) {
                        errorMessage = "Please enter the owner full name"
                        return@Button
                    }
                    if (mobileNumber.length < 10) {
                        errorMessage = "Please enter a valid 10-digit mobile number"
                        return@Button
                    }
                    if (password.length < 4) {
                        errorMessage = "Password must be at least 4 characters"
                        return@Button
                    }
                    isLoading = true
                    errorMessage = null
                    coroutineScope.launch {
                        val result = salonRepository.registerOwner(
                            salonName = salonName,
                            ownerName = ownerName,
                            mobileNumber = mobileNumber,
                            password = password,
                            businessType = businessType
                        )
                        isLoading = false
                        result.onSuccess { salon ->
                            sessionManager.saveOwnerSession(
                                salonId = salon.salonId,
                                salonName = salon.salonName,
                                mobileNumber = salon.mobileNumber
                            )
                            onRegisterSuccess(salon.salonId)
                        }.onFailure { ex ->
                            errorMessage = ex.message ?: "Registration failed."
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_owner_register_submit"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = BlackBackground
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = BlackBackground,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Register Salon & Go to Dashboard",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already registered? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = PinkPrimary,
                    modifier = Modifier
                        .clickable(onClick = onNavigateToLogin)
                        .testTag("link_owner_to_login")
                        .padding(4.dp)
                )
            }
        }
    }
}
