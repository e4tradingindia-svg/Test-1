package com.example.ui.owner

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.SalonMateLogoBadge
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldGradientEnd
import com.example.ui.theme.GoldGradientStart
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldTagBg
import com.example.ui.theme.LuxuryBlack
import com.example.ui.theme.LuxuryBlackElevated
import com.example.ui.theme.LuxurySurface
import com.example.ui.theme.MontserratFamily
import com.example.ui.theme.OwnerCardBg
import com.example.ui.theme.PinkBorder
import com.example.ui.theme.TextIvory
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class OwnerBusinessProfile(
  val ownerId: String,
  val ownerName: String,
  val salonName: String,
  val mobileNumber: String,
  val businessType: String
)

enum class OwnerAuthTab {
  SIGN_IN,
  REGISTER
}

@Composable
fun SalonOwnerAuthScreen(
  modifier: Modifier = Modifier,
  initialTab: OwnerAuthTab = OwnerAuthTab.SIGN_IN,
  onAuthSuccess: (OwnerBusinessProfile) -> Unit = {},
  onBackClick: () -> Unit = {}
) {
  BackHandler { onBackClick() }

  var currentTab by remember { mutableStateOf(initialTab) }
  val scrollState = rememberScrollState()
  val focusManager = LocalFocusManager.current
  val coroutineScope = rememberCoroutineScope()

  // Form Fields
  var ownerName by remember { mutableStateOf("") }
  var salonName by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var selectedType by remember { mutableStateOf("Men Salon") }
  var isPasswordVisible by remember { mutableStateOf(false) }
  var isConfirmPasswordVisible by remember { mutableStateOf(false) }

  // Status
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var isLoading by remember { mutableStateOf(false) }

  val businessTypes = listOf(
    stringResource(R.string.owner_type_men),
    stringResource(R.string.owner_type_women),
    stringResource(R.string.owner_type_unisex)
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(LuxuryBlack)
      .statusBarsPadding()
      .navigationBarsPadding()
      .imePadding()
      .testTag("owner_auth_screen_root"),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 520.dp)
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Bar: Back button
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 12.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBackClick,
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(LuxuryBlackElevated)
            .border(1.dp, GoldBorder, CircleShape)
            .testTag("owner_auth_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back to Welcome Screen",
            tint = GoldPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.weight(1f))
      }

      // SalonMate Emblem Logo Badge
      SalonMateLogoBadge(
        modifier = Modifier
          .size(92.dp)
          .testTag("owner_auth_logo_badge")
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Header Tag
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(GoldTagBg)
          .border(0.5.dp, GoldBorder, RoundedCornerShape(12.dp))
          .padding(horizontal = 12.dp, vertical = 4.dp)
      ) {
        Text(
          text = stringResource(R.string.owner_portal_tag),
          color = GoldPrimary,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp,
          fontFamily = MontserratFamily
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Welcome Header
      Text(
        text = stringResource(R.string.owner_welcome_title),
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        color = TextWhite,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = stringResource(R.string.owner_welcome_subtitle),
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        color = TextSecondary,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Tab Switcher: Partner Sign In vs Register Business
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(LuxurySurface)
          .border(1.dp, GoldBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
          .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        // Sign In Tab Button
        val signInSelected = currentTab == OwnerAuthTab.SIGN_IN
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(9.dp))
            .background(
              if (signInSelected) Brush.horizontalGradient(
                listOf(GoldGradientStart, GoldGradientEnd)
              ) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
            )
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = ripple(color = Color.White)
            ) {
              currentTab = OwnerAuthTab.SIGN_IN
              errorMessage = null
            }
            .padding(vertical = 10.dp)
            .testTag("owner_tab_sign_in"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = stringResource(R.string.owner_tab_sign_in),
            fontFamily = MontserratFamily,
            fontWeight = if (signInSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp,
            color = if (signInSelected) Color(0xFF1E1302) else TextSecondary
          )
        }

        // Register Business Tab Button
        val registerSelected = currentTab == OwnerAuthTab.REGISTER
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(9.dp))
            .background(
              if (registerSelected) Brush.horizontalGradient(
                listOf(GoldPrimary, GoldAccent)
              ) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
            )
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = ripple(color = Color.White)
            ) {
              currentTab = OwnerAuthTab.REGISTER
              errorMessage = null
            }
            .padding(vertical = 10.dp)
            .testTag("owner_tab_register"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = stringResource(R.string.owner_tab_register),
            fontFamily = MontserratFamily,
            fontWeight = if (registerSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp,
            color = if (registerSelected) Color(0xFF1E1302) else TextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Error Message Display
      if (errorMessage != null) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF330B14))
            .border(1.dp, PinkBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("owner_auth_error_banner")
        ) {
          Text(
            text = errorMessage.orEmpty(),
            fontFamily = MontserratFamily,
            fontSize = 12.sp,
            color = Color(0xFFFFB4C0),
            textAlign = TextAlign.Start
          )
        }
        Spacer(modifier = Modifier.height(14.dp))
      }

      // Fields Card Container (Warm Gold Border & Luxury Surface)
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(OwnerCardBg)
          .border(
            BorderStroke(1.5.dp, GoldBorder),
            RoundedCornerShape(16.dp)
          )
          .padding(18.dp)
      ) {
        // Salon / Parlour Name (Register only)
        if (currentTab == OwnerAuthTab.REGISTER) {
          Text(
            text = stringResource(R.string.owner_label_salon_name),
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = TextIvory
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = salonName,
            onValueChange = {
              salonName = it
              errorMessage = null
            },
            placeholder = {
              Text(
                text = stringResource(R.string.owner_hint_salon_name),
                color = TextMuted,
                fontSize = 14.sp
              )
            },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Storefront,
                contentDescription = null,
                tint = GoldPrimary
              )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Text,
              imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
              onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = TextWhite,
              unfocusedTextColor = TextWhite,
              focusedBorderColor = GoldPrimary,
              unfocusedBorderColor = LuxurySurface,
              focusedContainerColor = LuxuryBlackElevated,
              unfocusedContainerColor = LuxuryBlackElevated
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("owner_input_salon_name")
          )
          Spacer(modifier = Modifier.height(14.dp))

          // Owner Full Name
          Text(
            text = stringResource(R.string.label_full_name),
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = TextIvory
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = ownerName,
            onValueChange = {
              ownerName = it
              errorMessage = null
            },
            placeholder = {
              Text(
                text = "Owner / Manager Full Name",
                color = TextMuted,
                fontSize = 14.sp
              )
            },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = GoldPrimary
              )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Text,
              imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
              onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = TextWhite,
              unfocusedTextColor = TextWhite,
              focusedBorderColor = GoldPrimary,
              unfocusedBorderColor = LuxurySurface,
              focusedContainerColor = LuxuryBlackElevated,
              unfocusedContainerColor = LuxuryBlackElevated
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("owner_input_full_name")
          )
          Spacer(modifier = Modifier.height(14.dp))

          // Business Type Selector
          Text(
            text = stringResource(R.string.owner_label_business_type),
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = TextIvory
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            businessTypes.forEach { type ->
              val isSelected = selectedType == type
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) GoldPrimary else LuxuryBlackElevated)
                  .border(
                    1.dp,
                    if (isSelected) GoldAccent else LuxurySurface,
                    RoundedCornerShape(8.dp)
                  )
                  .clickable { selectedType = type }
                  .padding(vertical = 8.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = type,
                  fontFamily = MontserratFamily,
                  fontSize = 10.5.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) Color(0xFF1E1302) else TextSecondary,
                  textAlign = TextAlign.Center,
                  maxLines = 1
                )
              }
            }
          }
          Spacer(modifier = Modifier.height(14.dp))
        }

        // Mobile Number
        Text(
          text = stringResource(R.string.label_mobile_number),
          fontFamily = MontserratFamily,
          fontWeight = FontWeight.Medium,
          fontSize = 13.sp,
          color = TextIvory
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = phone,
          onValueChange = { input ->
            phone = input.filter { it.isDigit() || it == '+' || it == '-' || it == ' ' }
            errorMessage = null
          },
          placeholder = {
            Text(
              text = stringResource(R.string.hint_mobile_number),
              color = TextMuted,
              fontSize = 14.sp
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Phone,
              contentDescription = null,
              tint = GoldPrimary
            )
          },
          singleLine = true,
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next
          ),
          keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
          ),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            focusedBorderColor = GoldPrimary,
            unfocusedBorderColor = LuxurySurface,
            focusedContainerColor = LuxuryBlackElevated,
            unfocusedContainerColor = LuxuryBlackElevated
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("owner_input_phone")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Password Field
        Text(
          text = stringResource(R.string.label_password),
          fontFamily = MontserratFamily,
          fontWeight = FontWeight.Medium,
          fontSize = 13.sp,
          color = TextIvory
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = password,
          onValueChange = {
            password = it
            errorMessage = null
          },
          placeholder = {
            Text(
              text = stringResource(R.string.hint_password),
              color = TextMuted,
              fontSize = 14.sp
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = GoldPrimary
            )
          },
          trailingIcon = {
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
              Icon(
                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                tint = TextSecondary
              )
            }
          },
          visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
          singleLine = true,
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = if (currentTab == OwnerAuthTab.REGISTER) ImeAction.Next else ImeAction.Done
          ),
          keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onDone = { focusManager.clearFocus() }
          ),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            focusedBorderColor = GoldPrimary,
            unfocusedBorderColor = LuxurySurface,
            focusedContainerColor = LuxuryBlackElevated,
            unfocusedContainerColor = LuxuryBlackElevated
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("owner_input_password")
        )

        // Confirm Password (Register only)
        if (currentTab == OwnerAuthTab.REGISTER) {
          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = stringResource(R.string.label_confirm_password),
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = TextIvory
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
              confirmPassword = it
              errorMessage = null
            },
            placeholder = {
              Text(
                text = stringResource(R.string.hint_confirm_password),
                color = TextMuted,
                fontSize = 14.sp
              )
            },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = GoldPrimary
              )
            },
            trailingIcon = {
              IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                Icon(
                  imageVector = if (isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                  contentDescription = if (isConfirmPasswordVisible) "Hide password" else "Show password",
                  tint = TextSecondary
                )
              }
            },
            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Password,
              imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
              onDone = { focusManager.clearFocus() }
            ),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = TextWhite,
              unfocusedTextColor = TextWhite,
              focusedBorderColor = GoldPrimary,
              unfocusedBorderColor = LuxurySurface,
              focusedContainerColor = LuxuryBlackElevated,
              unfocusedContainerColor = LuxuryBlackElevated
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("owner_input_confirm_password")
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Submit Button
        Button(
          onClick = {
            focusManager.clearFocus()
            errorMessage = null

            val rawDigits = phone.filter { it.isDigit() }
            if (rawDigits.length < 10) {
              errorMessage = "Please enter a valid 10-digit mobile number."
              return@Button
            }
            if (password.length < 6) {
              errorMessage = "Password must be at least 6 characters."
              return@Button
            }

            if (currentTab == OwnerAuthTab.REGISTER) {
              if (salonName.trim().isEmpty()) {
                errorMessage = "Please enter your Salon / Parlour name."
                return@Button
              }
              if (ownerName.trim().isEmpty()) {
                errorMessage = "Please enter the owner / manager name."
                return@Button
              }
              if (password != confirmPassword) {
                errorMessage = "Passwords do not match."
                return@Button
              }
            }

            isLoading = true
            coroutineScope.launch {
              delay(400) // Brief smooth interaction feel
              isLoading = false
              val resolvedSalonName = if (salonName.isNotBlank()) salonName else "Elite Studio & Spa"
              val resolvedOwnerName = if (ownerName.isNotBlank()) ownerName else "Salon Partner"
              val profile = OwnerBusinessProfile(
                ownerId = "OWNER-${rawDigits.takeLast(4)}",
                ownerName = resolvedOwnerName,
                salonName = resolvedSalonName,
                mobileNumber = rawDigits,
                businessType = selectedType
              )
              onAuthSuccess(profile)
            }
          },
          enabled = !isLoading,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(
              Brush.horizontalGradient(listOf(GoldGradientStart, GoldPrimary, GoldAccent)),
              RoundedCornerShape(12.dp)
            )
            .testTag("owner_auth_submit_button")
        ) {
          if (isLoading) {
            CircularProgressIndicator(
              color = Color(0xFF1E1302),
              modifier = Modifier.size(22.dp),
              strokeWidth = 2.dp
            )
          } else {
            Text(
              text = if (currentTab == OwnerAuthTab.SIGN_IN) {
                stringResource(R.string.owner_btn_sign_in)
              } else {
                stringResource(R.string.owner_btn_register)
              },
              fontFamily = MontserratFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = Color(0xFF1E1302)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Switch Tab Link
      Text(
        text = if (currentTab == OwnerAuthTab.SIGN_IN) {
          stringResource(R.string.owner_link_new)
        } else {
          stringResource(R.string.owner_link_existing)
        },
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        color = GoldPrimary,
        modifier = Modifier
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = GoldPrimary)
          ) {
            currentTab = if (currentTab == OwnerAuthTab.SIGN_IN) {
              OwnerAuthTab.REGISTER
            } else {
              OwnerAuthTab.SIGN_IN
            }
            errorMessage = null
          }
          .padding(8.dp)
          .testTag("owner_switch_tab_link")
      )

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
