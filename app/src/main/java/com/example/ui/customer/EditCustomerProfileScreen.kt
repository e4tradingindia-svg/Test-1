package com.example.ui.customer

import android.app.Activity
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CustomerEntity
import com.example.data.repository.CustomerRepository
import com.example.ui.components.SalonMateLogoBadge
import com.example.ui.theme.CustomerCardBg
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
import com.example.ui.theme.PinkBorder
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.TextIvory
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun EditCustomerProfileScreen(
  customer: CustomerEntity,
  repository: CustomerRepository,
  onProfileUpdated: (CustomerEntity) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val context = LocalContext.current
  val focusManager = LocalFocusManager.current
  val coroutineScope = rememberCoroutineScope()

  var currentPhotoUriString by remember { mutableStateOf(customer.profilePhotoUri) }
  var addressInput by remember { mutableStateOf(customer.address ?: "") }
  var isSaving by remember { mutableStateOf(false) }
  var feedbackMessage by remember { mutableStateOf<String?>(null) }
  var isErrorMessage by remember { mutableStateOf(false) }

  // Zero-permission Android Photo Picker for picking a profile picture
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      coroutineScope.launch {
        // Copy picked image to internal app storage so it permanently persists across app launches
        val savedFileUri = withContext(Dispatchers.IO) {
          try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            val profilePicFile = File(context.filesDir, "profile_${customer.customerId}.jpg")
            val outputStream = FileOutputStream(profilePicFile)
            inputStream?.use { input ->
              outputStream.use { output ->
                input.copyTo(output)
              }
            }
            Uri.fromFile(profilePicFile).toString()
          } catch (e: Exception) {
            uri.toString()
          }
        }
        currentPhotoUriString = savedFileUri
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(LuxuryBlack)
      .statusBarsPadding()
      .navigationBarsPadding()
      .imePadding()
      .testTag("edit_profile_screen_root"),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 540.dp)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Bar: Back Button & Title
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(LuxuryBlackElevated)
            .border(1.dp, GoldBorder.copy(alpha = 0.6f), CircleShape)
            .testTag("edit_profile_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back to Portal",
            tint = GoldPrimary,
            modifier = Modifier.size(20.dp)
          )
        }

        Text(
          text = stringResource(R.string.title_edit_profile),
          fontFamily = MontserratFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = TextWhite
        )

        // Spacer to balance the top bar layout
        Spacer(modifier = Modifier.size(42.dp))
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Profile Photo Avatar Section
      Box(
        modifier = Modifier
          .size(110.dp)
          .clip(CircleShape)
          .background(LuxuryBlackElevated)
          .border(2.dp, GoldPrimary, CircleShape)
          .clickable {
            photoPickerLauncher.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
          }
          .testTag("profile_avatar_box"),
        contentAlignment = Alignment.Center
      ) {
        val photoUri = currentPhotoUriString
        var loadedBitmap by remember(photoUri) {
          mutableStateOf<android.graphics.Bitmap?>(null)
        }

        // Load image bitmap safely from local URI if present
        if (photoUri != null) {
          remember(photoUri) {
            try {
              val uriParsed = Uri.parse(photoUri)
              if (uriParsed.scheme == "file") {
                val file = File(uriParsed.path ?: "")
                if (file.exists()) {
                  loadedBitmap = BitmapFactory.decodeFile(file.absolutePath)
                }
              } else {
                context.contentResolver.openInputStream(uriParsed)?.use { stream ->
                  loadedBitmap = BitmapFactory.decodeStream(stream)
                }
              }
            } catch (e: Exception) {
              loadedBitmap = null
            }
            true
          }
        }

        val bitmap = loadedBitmap
        if (bitmap != null) {
          Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Profile Photo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        } else {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Default Avatar",
            tint = GoldPrimary,
            modifier = Modifier.size(54.dp)
          )
        }

        // Camera overlay badge
        Box(
          modifier = Modifier
            .size(32.dp)
            .align(Alignment.BottomEnd)
            .clip(CircleShape)
            .background(PinkPrimary)
            .border(1.5.dp, LuxuryBlack, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = "Change Photo",
            tint = TextWhite,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Photo Action Buttons
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
          onClick = {
            photoPickerLauncher.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
          },
          border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.7f)),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
          modifier = Modifier.testTag("btn_choose_photo")
        ) {
          Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = GoldPrimary
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = stringResource(R.string.btn_change_photo),
            fontFamily = MontserratFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }

        if (!currentPhotoUriString.isNullOrEmpty()) {
          OutlinedButton(
            onClick = { currentPhotoUriString = null },
            border = BorderStroke(1.dp, PinkBorder.copy(alpha = 0.7f)),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF8FA3)),
            modifier = Modifier.testTag("btn_remove_photo")
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = Color(0xFFFF8FA3)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = stringResource(R.string.btn_remove_photo),
              fontFamily = MontserratFamily,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Feedback / Status Banner
      if (feedbackMessage != null) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isErrorMessage) Color(0xFF330B14) else Color(0xFF0F2618))
            .border(
              1.dp,
              if (isErrorMessage) PinkBorder else Color(0xFF2E7D32),
              RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("edit_profile_feedback_banner")
        ) {
          Text(
            text = feedbackMessage.orEmpty(),
            fontFamily = MontserratFamily,
            fontSize = 12.sp,
            color = if (isErrorMessage) Color(0xFFFFB4C0) else Color(0xFFA5D6A7),
            textAlign = TextAlign.Start
          )
        }
        Spacer(modifier = Modifier.height(14.dp))
      }

      // Profile Fields Card Container
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CustomerCardBg),
        border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f))
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          // Read-Only: Full Name
          Text(
            text = stringResource(R.string.label_full_name),
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(4.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(LuxuryBlackElevated)
              .border(0.5.dp, LuxurySurface, RoundedCornerShape(10.dp))
              .padding(horizontal = 14.dp, vertical = 12.dp)
          ) {
            Text(
              text = customer.fullName,
              fontFamily = MontserratFamily,
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp,
              color = TextWhite,
              modifier = Modifier.testTag("edit_profile_name_readonly")
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Read-Only: Customer ID & Mobile Number Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Customer ID
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = stringResource(R.string.portal_customer_id),
                fontFamily = MontserratFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = TextSecondary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(LuxuryBlackElevated)
                  .border(0.5.dp, LuxurySurface, RoundedCornerShape(10.dp))
                  .padding(horizontal = 12.dp, vertical = 12.dp)
              ) {
                Text(
                  text = customer.customerId,
                  fontFamily = MontserratFamily,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 13.sp,
                  color = GoldPrimary,
                  modifier = Modifier.testTag("edit_profile_id_readonly")
                )
              }
            }

            // Mobile Number
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = stringResource(R.string.portal_phone),
                fontFamily = MontserratFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = TextSecondary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(LuxuryBlackElevated)
                  .border(0.5.dp, LuxurySurface, RoundedCornerShape(10.dp))
                  .padding(horizontal = 12.dp, vertical = 12.dp)
              ) {
                Text(
                  text = customer.normalizedPhone,
                  fontFamily = MontserratFamily,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 13.sp,
                  color = TextWhite,
                  modifier = Modifier.testTag("edit_profile_phone_readonly")
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Editable: Address (Optional)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = stringResource(R.string.label_address),
              fontFamily = MontserratFamily,
              fontWeight = FontWeight.Medium,
              fontSize = 13.sp,
              color = TextIvory
            )

            if (addressInput.isNotEmpty()) {
              Text(
                text = "Clear",
                fontFamily = MontserratFamily,
                fontSize = 12.sp,
                color = PinkPrimary,
                modifier = Modifier
                  .clickable { addressInput = "" }
                  .testTag("btn_clear_address")
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          OutlinedTextField(
            value = addressInput,
            onValueChange = {
              addressInput = it
              feedbackMessage = null
            },
            placeholder = {
              Text(
                text = stringResource(R.string.hint_address),
                color = TextMuted,
                fontSize = 13.sp
              )
            },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = GoldPrimary
              )
            },
            minLines = 2,
            maxLines = 4,
            keyboardOptions = KeyboardOptions(
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
              .testTag("input_customer_address")
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Save Changes Button
      Button(
        onClick = {
          focusManager.clearFocus()
          isSaving = true
          feedbackMessage = null

          coroutineScope.launch {
            val result = repository.updateCustomerProfile(
              customerId = customer.customerId,
              photoUri = currentPhotoUriString,
              address = addressInput
            )
            isSaving = false
            result.fold(
              onSuccess = { updatedCustomer ->
                isErrorMessage = false
                feedbackMessage = "Profile updated successfully!"
                onProfileUpdated(updatedCustomer)
              },
              onFailure = { error ->
                isErrorMessage = true
                feedbackMessage = error.message ?: "Failed to update profile."
              }
            )
          }
        },
        enabled = !isSaving,
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
          .testTag("btn_save_profile_changes")
      ) {
        if (isSaving) {
          CircularProgressIndicator(
            color = Color(0xFF1E1302),
            modifier = Modifier.size(22.dp),
            strokeWidth = 2.dp
          )
        } else {
          Text(
            text = stringResource(R.string.btn_save_changes),
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color(0xFF1E1302)
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
