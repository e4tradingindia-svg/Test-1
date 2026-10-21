package com.example.ui.customer

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.CustomerEntity
import com.example.data.repository.CustomerRepository
import com.example.ui.components.SalonMateLogoBadge
import com.example.ui.theme.CustomerCardBg
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldGradientEnd
import com.example.ui.theme.GoldGradientStart
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LuxuryBlack
import com.example.ui.theme.LuxuryBlackElevated
import com.example.ui.theme.LuxurySurface
import com.example.ui.theme.MontserratFamily
import com.example.ui.theme.PinkBorder
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.TextIvory
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.launch

enum class CustomerAuthTab {
  SIGN_IN,
  CREATE_ACCOUNT
}

@Composable
fun CustomerAuthScreen(
  repository: CustomerRepository,
  modifier: Modifier = Modifier,
  initialTab: CustomerAuthTab = CustomerAuthTab.SIGN_IN,
  initialRememberMe: Boolean = true,
  onAuthSuccess: (CustomerEntity, Boolean) -> Unit = { _, _ -> },
  onBackClick: () -> Unit = {}
) {
  BackHandler { onBackClick() }

  var currentTab by remember { mutableStateOf(initialTab) }
  val scrollState = rememberScrollState()
  val focusManager = LocalFocusManager.current
  val coroutineScope = rememberCoroutineScope()

  // Form Fields
  var fullName by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var rememberMe by remember { mutableStateOf(initialRememberMe) }
  var isPasswordVisible by remember { mutableStateOf(false) }
  var isConfirmPasswordVisible by remember { mutableStateOf(false) }

  // Status
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var isLoading by remember { mutableStateOf(false) }

  // Legal Dialogs
  var showTermsDialog by remember { mutableStateOf(false) }
  var showPrivacyDialog by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(LuxuryBlack)
      .statusBarsPadding()
      .navigationBarsPadding()
      .imePadding()
      .testTag("customer_auth_screen_root"),
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
            .testTag("auth_back_button")
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
          .testTag("auth_logo_badge")
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Welcome Header
      Text(
        text = stringResource(R.string.customer_welcome_title),
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        color = TextWhite,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = stringResource(R.string.customer_welcome_subtitle),
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        color = TextSecondary,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Tab Switcher: Sign In vs Create Account
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
        val signInSelected = currentTab == CustomerAuthTab.SIGN_IN
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
              currentTab = CustomerAuthTab.SIGN_IN
              errorMessage = null
            }
            .padding(vertical = 10.dp)
            .testTag("tab_sign_in_button"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = stringResource(R.string.tab_sign_in),
            fontFamily = MontserratFamily,
            fontWeight = if (signInSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp,
            color = if (signInSelected) Color(0xFF1E1302) else TextSecondary
          )
        }

        // Create Account Tab Button
        val createSelected = currentTab == CustomerAuthTab.CREATE_ACCOUNT
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(9.dp))
            .background(
              if (createSelected) Brush.horizontalGradient(
                listOf(PinkPrimary, Color(0xFFC2185B))
              ) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
            )
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = ripple(color = Color.White)
            ) {
              currentTab = CustomerAuthTab.CREATE_ACCOUNT
              errorMessage = null
            }
            .padding(vertical = 10.dp)
            .testTag("tab_create_account_button"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = stringResource(R.string.tab_create_account),
            fontFamily = MontserratFamily,
            fontWeight = if (createSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp,
            color = if (createSelected) TextWhite else TextSecondary
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
            .testTag("auth_error_banner")
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

      // Fields Card Container
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(CustomerCardBg)
          .border(
            BorderStroke(
              1.dp,
              if (currentTab == CustomerAuthTab.SIGN_IN) GoldBorder else PinkBorder
            ),
            RoundedCornerShape(16.dp)
          )
          .padding(18.dp)
      ) {
        // Full Name (Only for Create Account)
        if (currentTab == CustomerAuthTab.CREATE_ACCOUNT) {
          Text(
            text = stringResource(R.string.label_full_name),
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = TextIvory
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = fullName,
            onValueChange = {
              fullName = it
              errorMessage = null
            },
            placeholder = {
              Text(
                text = stringResource(R.string.hint_full_name),
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
              focusedBorderColor = PinkPrimary,
              unfocusedBorderColor = LuxurySurface,
              focusedContainerColor = LuxuryBlackElevated,
              unfocusedContainerColor = LuxuryBlackElevated
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("auth_input_full_name")
          )
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
            // Allow typing digits only
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
            focusedBorderColor = if (currentTab == CustomerAuthTab.SIGN_IN) GoldPrimary else PinkPrimary,
            unfocusedBorderColor = LuxurySurface,
            focusedContainerColor = LuxuryBlackElevated,
            unfocusedContainerColor = LuxuryBlackElevated
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("auth_input_mobile_number")
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
            imeAction = if (currentTab == CustomerAuthTab.CREATE_ACCOUNT) ImeAction.Next else ImeAction.Done
          ),
          keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onDone = { focusManager.clearFocus() }
          ),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            focusedBorderColor = if (currentTab == CustomerAuthTab.SIGN_IN) GoldPrimary else PinkPrimary,
            unfocusedBorderColor = LuxurySurface,
            focusedContainerColor = LuxuryBlackElevated,
            unfocusedContainerColor = LuxuryBlackElevated
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("auth_input_password")
        )

        // Confirm Password (Only for Create Account)
        if (currentTab == CustomerAuthTab.CREATE_ACCOUNT) {
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
              focusedBorderColor = PinkPrimary,
              unfocusedBorderColor = LuxurySurface,
              focusedContainerColor = LuxuryBlackElevated,
              unfocusedContainerColor = LuxuryBlackElevated
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("auth_input_confirm_password")
          )
        }

        // Remember Me checkbox (Only for Sign In)
        if (currentTab == CustomerAuthTab.SIGN_IN) {
          Spacer(modifier = Modifier.height(12.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
              ) { rememberMe = !rememberMe },
            verticalAlignment = Alignment.CenterVertically
          ) {
            Checkbox(
              checked = rememberMe,
              onCheckedChange = { rememberMe = it },
              colors = CheckboxDefaults.colors(
                checkedColor = GoldPrimary,
                checkmarkColor = Color(0xFF1E1302),
                uncheckedColor = TextSecondary
              ),
              modifier = Modifier.testTag("auth_remember_me_checkbox")
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = stringResource(R.string.label_remember_me),
              fontFamily = MontserratFamily,
              fontWeight = FontWeight.Medium,
              fontSize = 13.sp,
              color = TextIvory
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Submit Button
        Button(
          onClick = {
            focusManager.clearFocus()
            errorMessage = null
            isLoading = true

            coroutineScope.launch {
              if (currentTab == CustomerAuthTab.SIGN_IN) {
                val result = repository.authenticateCustomer(
                  rawPhone = phone,
                  password = password
                )
                isLoading = false
                result.fold(
                  onSuccess = { customer ->
                    onAuthSuccess(customer, rememberMe)
                  },
                  onFailure = { err ->
                    errorMessage = err.message ?: "Authentication failed. Please check your credentials."
                  }
                )
              } else {
                val result = repository.registerCustomer(
                  fullName = fullName,
                  rawPhone = phone,
                  password = password,
                  confirmPassword = confirmPassword
                )
                isLoading = false
                result.fold(
                  onSuccess = { customer ->
                    onAuthSuccess(customer, true)
                  },
                  onFailure = { err ->
                    errorMessage = err.message ?: "Failed to create account. Please try again."
                  }
                )
              }
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
              if (currentTab == CustomerAuthTab.SIGN_IN) {
                Brush.horizontalGradient(listOf(GoldGradientStart, GoldGradientEnd))
              } else {
                Brush.horizontalGradient(listOf(PinkPrimary, Color(0xFFC2185B)))
              },
              RoundedCornerShape(12.dp)
            )
            .testTag(
              if (currentTab == CustomerAuthTab.SIGN_IN) "customer_sign_in_submit"
              else "customer_create_account_submit"
            )
        ) {
          if (isLoading) {
            CircularProgressIndicator(
              color = if (currentTab == CustomerAuthTab.SIGN_IN) Color(0xFF1E1302) else TextWhite,
              modifier = Modifier.size(22.dp),
              strokeWidth = 2.dp
            )
          } else {
            Text(
              text = if (currentTab == CustomerAuthTab.SIGN_IN) {
                stringResource(R.string.btn_sign_in)
              } else {
                stringResource(R.string.btn_create_account)
              },
              fontFamily = MontserratFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = if (currentTab == CustomerAuthTab.SIGN_IN) Color(0xFF1E1302) else TextWhite
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Switch Tab Link
      Text(
        text = if (currentTab == CustomerAuthTab.SIGN_IN) {
          stringResource(R.string.link_new_to_salonmate)
        } else {
          stringResource(R.string.link_already_have_account)
        },
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        color = if (currentTab == CustomerAuthTab.SIGN_IN) PinkPrimary else GoldPrimary,
        modifier = Modifier
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = GoldPrimary)
          ) {
            currentTab = if (currentTab == CustomerAuthTab.SIGN_IN) {
              CustomerAuthTab.CREATE_ACCOUNT
            } else {
              CustomerAuthTab.SIGN_IN
            }
            errorMessage = null
          }
          .padding(8.dp)
          .testTag(
            if (currentTab == CustomerAuthTab.SIGN_IN) "switch_to_create_account"
            else "switch_to_sign_in"
          )
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Legal Terms & Privacy Notice
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center
      ) {
        TextButton(
          onClick = { showTermsDialog = true },
          modifier = Modifier.testTag("auth_terms_link")
        ) {
          Text(
            text = "Terms of Service",
            fontFamily = MontserratFamily,
            fontSize = 11.sp,
            color = TextSecondary
          )
        }
        Text(
          text = "•",
          color = TextMuted,
          fontSize = 11.sp,
          modifier = Modifier.align(Alignment.CenterVertically)
        )
        TextButton(
          onClick = { showPrivacyDialog = true },
          modifier = Modifier.testTag("auth_privacy_link")
        ) {
          Text(
            text = "Privacy Policy",
            fontFamily = MontserratFamily,
            fontSize = 11.sp,
            color = TextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Terms Dialog
  if (showTermsDialog) {
    AlertDialog(
      onDismissRequest = { showTermsDialog = false },
      containerColor = LuxurySurface,
      title = {
        Text(
          text = stringResource(R.string.dialog_terms_title),
          fontFamily = MontserratFamily,
          fontWeight = FontWeight.Bold,
          color = GoldPrimary
        )
      },
      text = {
        Text(
          text = stringResource(R.string.dialog_terms_content),
          fontFamily = MontserratFamily,
          fontSize = 13.sp,
          color = TextIvory
        )
      },
      confirmButton = {
        TextButton(onClick = { showTermsDialog = false }) {
          Text(
            text = stringResource(R.string.btn_close),
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.Bold,
            color = GoldPrimary
          )
        }
      }
    )
  }

  // Privacy Policy Dialog
  if (showPrivacyDialog) {
    AlertDialog(
      onDismissRequest = { showPrivacyDialog = false },
      containerColor = LuxurySurface,
      title = {
        Text(
          text = stringResource(R.string.dialog_privacy_title),
          fontFamily = MontserratFamily,
          fontWeight = FontWeight.Bold,
          color = PinkPrimary
        )
      },
      text = {
        Text(
          text = stringResource(R.string.dialog_privacy_content),
          fontFamily = MontserratFamily,
          fontSize = 13.sp,
          color = TextIvory
        )
      },
      confirmButton = {
        TextButton(onClick = { showPrivacyDialog = false }) {
          Text(
            text = stringResource(R.string.btn_close),
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.Bold,
            color = PinkPrimary
          )
        }
      }
    )
  }
}
