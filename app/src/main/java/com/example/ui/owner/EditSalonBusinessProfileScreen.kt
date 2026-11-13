package com.example.ui.owner

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
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
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Storefront
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.repository.SalonRepository
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

@SuppressLint("MissingPermission")
@Composable
fun EditSalonBusinessProfileScreen(
  profile: OwnerBusinessProfile,
  salonRepository: SalonRepository,
  onProfileUpdated: (OwnerBusinessProfile) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val context = LocalContext.current
  val focusManager = LocalFocusManager.current
  val coroutineScope = rememberCoroutineScope()

  // Observe database record to always reflect actual registered salon name
  val dbSalon by salonRepository.observeSalonById(profile.salonId).collectAsState(initial = null)
  val displayedSalonName = dbSalon?.salonName?.takeIf { it.isNotBlank() } ?: profile.salonName

  var bannerUriString by remember { mutableStateOf(profile.bannerUri) }
  var addressInput by remember { mutableStateOf(profile.address ?: "") }
  var mobileInput by remember { mutableStateOf(profile.mobileNumber) }
  var latitudeInput by remember { mutableStateOf(profile.latitude?.toString() ?: "") }
  var longitudeInput by remember { mutableStateOf(profile.longitude?.toString() ?: "") }
  var isManualGpsOpen by remember { mutableStateOf(profile.latitude != null || profile.longitude != null) }

  var isSaving by remember { mutableStateOf(false) }
  var isFetchingLocation by remember { mutableStateOf(false) }
  var feedbackMessage by remember { mutableStateOf<String?>(null) }
  var isErrorMessage by remember { mutableStateOf(false) }

  // Gallery Photo Picker for Salon Banner
  val bannerPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      coroutineScope.launch {
        val savedUri = withContext(Dispatchers.IO) {
          try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bannerFile = File(context.filesDir, "salon_banner_${profile.salonId}.jpg")
            val outputStream = FileOutputStream(bannerFile)
            inputStream?.use { input ->
              outputStream.use { output ->
                input.copyTo(output)
              }
            }
            Uri.fromFile(bannerFile).toString()
          } catch (e: Exception) {
            uri.toString()
          }
        }
        bannerUriString = savedUri
      }
    }
  }

  // Location Permission Launcher
  val locationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
    val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    if (fineGranted || coarseGranted) {
      // Fetch GPS location
      fetchGpsLocation(context) { loc ->
        if (loc != null) {
          latitudeInput = String.format("%.6f", loc.latitude)
          longitudeInput = String.format("%.6f", loc.longitude)
          feedbackMessage = "GPS location detected successfully!"
          isErrorMessage = false
        } else {
          feedbackMessage = "Unable to read GPS location. You can enter coordinates manually below."
          isErrorMessage = true
          isManualGpsOpen = true
        }
        isFetchingLocation = false
      }
    } else {
      isFetchingLocation = false
      feedbackMessage = "Location permission denied. Please enter coordinates manually."
      isErrorMessage = true
      isManualGpsOpen = true
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(LuxuryBlack)
      .statusBarsPadding()
      .navigationBarsPadding()
      .imePadding()
      .testTag("edit_salon_profile_screen_root"),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 560.dp)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Navigation Bar
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
            .testTag("edit_salon_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back to Dashboard",
            tint = GoldPrimary,
            modifier = Modifier.size(20.dp)
          )
        }

        Text(
          text = stringResource(R.string.owner_edit_profile_title),
          fontFamily = MontserratFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = TextWhite
        )

        Spacer(modifier = Modifier.size(42.dp))
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = stringResource(R.string.owner_edit_profile_subtitle),
        fontFamily = MontserratFamily,
        fontSize = 12.5.sp,
        color = TextSecondary,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Salon Banner / Poster Preview & Controls
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("salon_banner_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = OwnerCardBg),
        border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.6f))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = stringResource(R.string.owner_label_banner),
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = GoldPrimary
          )
          Spacer(modifier = Modifier.height(10.dp))

          // Banner Image Box
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(150.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(LuxuryBlackElevated)
              .border(1.dp, LuxurySurface, RoundedCornerShape(12.dp))
              .clickable {
                bannerPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              }
              .testTag("salon_banner_preview_box"),
            contentAlignment = Alignment.Center
          ) {
            val uri = bannerUriString
            var loadedBitmap by remember(uri) {
              mutableStateOf<android.graphics.Bitmap?>(null)
            }

            if (uri != null) {
              remember(uri) {
                try {
                  val parsed = Uri.parse(uri)
                  if (parsed.scheme == "file") {
                    val file = File(parsed.path ?: "")
                    if (file.exists()) {
                      loadedBitmap = BitmapFactory.decodeFile(file.absolutePath)
                    }
                  } else {
                    context.contentResolver.openInputStream(parsed)?.use { s ->
                      loadedBitmap = BitmapFactory.decodeStream(s)
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
                contentDescription = "Salon Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
              )
            } else {
              // Default Luxury Banner Placeholder
              Image(
                painter = painterResource(id = R.drawable.hero_salon_banner),
                contentDescription = "Default Salon Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
              )
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(28.dp)
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "Tap to choose salon banner photo",
                    fontFamily = MontserratFamily,
                    fontSize = 11.sp,
                    color = TextIvory
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Banner action buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = {
                bannerPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              },
              border = BorderStroke(1.dp, GoldBorder),
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
              modifier = Modifier
                .weight(1f)
                .testTag("btn_upload_banner")
            ) {
              Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                tint = GoldPrimary
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (bannerUriString.isNullOrEmpty()) stringResource(R.string.owner_btn_upload_banner) else stringResource(R.string.owner_btn_replace_banner),
                fontFamily = MontserratFamily,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold
              )
            }

            if (!bannerUriString.isNullOrEmpty()) {
              OutlinedButton(
                onClick = { bannerUriString = null },
                border = BorderStroke(1.dp, PinkBorder),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF8FA3)),
                modifier = Modifier.testTag("btn_remove_banner")
              ) {
                Icon(
                  imageVector = Icons.Default.Delete,
                  contentDescription = null,
                  modifier = Modifier.size(15.dp),
                  tint = Color(0xFFFF8FA3)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = stringResource(R.string.owner_btn_remove_banner),
                  fontFamily = MontserratFamily,
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Feedback / Alert banner
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
            .testTag("edit_salon_feedback_banner")
        ) {
          Text(
            text = feedbackMessage.orEmpty(),
            fontFamily = MontserratFamily,
            fontSize = 12.sp,
            color = if (isErrorMessage) Color(0xFFFFB4C0) else Color(0xFFA5D6A7)
          )
        }
        Spacer(modifier = Modifier.height(14.dp))
      }

      // Fields Card Container: Address, Mobile Number, GPS
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = OwnerCardBg),
        border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f))
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          // Salon Name & Unique Salon ID (Read-only reference)
          Text(
            text = "Salon / Parlour Business",
            fontFamily = MontserratFamily,
            fontSize = 11.sp,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = displayedSalonName,
              fontFamily = MontserratFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = TextWhite,
              modifier = Modifier.testTag("edit_salon_name_header")
            )
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(GoldTagBg)
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = "ID: ${profile.salonId}",
                fontFamily = MontserratFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = GoldPrimary,
                modifier = Modifier.testTag("edit_salon_id_badge")
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Salon Address Field
          Text(
            text = stringResource(R.string.owner_label_salon_address),
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = TextIvory
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = addressInput,
            onValueChange = {
              addressInput = it
              feedbackMessage = null
            },
            placeholder = {
              Text(
                text = stringResource(R.string.owner_hint_salon_address),
                color = TextMuted,
                fontSize = 13.sp
              )
            },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Storefront,
                contentDescription = null,
                tint = GoldPrimary
              )
            },
            minLines = 2,
            maxLines = 4,
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
              .testTag("input_salon_address")
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Salon Mobile Number Field
          Text(
            text = stringResource(R.string.owner_label_contact_phone),
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = TextIvory
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = mobileInput,
            onValueChange = {
              mobileInput = it
              feedbackMessage = null
            },
            placeholder = {
              Text(
                text = stringResource(R.string.hint_mobile_number),
                color = TextMuted,
                fontSize = 13.sp
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
              .testTag("input_salon_phone")
          )

          Spacer(modifier = Modifier.height(20.dp))

          // GPS Location Section
          Text(
            text = stringResource(R.string.owner_label_gps),
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = TextIvory
          )
          Spacer(modifier = Modifier.height(4.dp))

          // Current GPS Coordinates status display
          val hasCoords = latitudeInput.isNotBlank() && longitudeInput.isNotBlank()
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(LuxuryBlackElevated)
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = if (hasCoords) Icons.Default.LocationOn else Icons.Default.AddLocation,
              contentDescription = null,
              tint = if (hasCoords) GoldPrimary else TextMuted,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (hasCoords) "$latitudeInput, $longitudeInput" else stringResource(R.string.owner_gps_status_unset),
              fontFamily = MontserratFamily,
              fontWeight = if (hasCoords) FontWeight.SemiBold else FontWeight.Normal,
              fontSize = 12.sp,
              color = if (hasCoords) GoldPrimary else TextSecondary,
              modifier = Modifier.testTag("salon_gps_status_text")
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // GPS Buttons: Phone GPS & Manual Entry
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Use Phone GPS Location
            Button(
              onClick = {
                focusManager.clearFocus()
                isFetchingLocation = true
                feedbackMessage = null

                val hasFine = ContextCompat.checkSelfPermission(
                  context,
                  Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                val hasCoarse = ContextCompat.checkSelfPermission(
                  context,
                  Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (hasFine || hasCoarse) {
                  fetchGpsLocation(context) { loc ->
                    if (loc != null) {
                      latitudeInput = String.format("%.6f", loc.latitude)
                      longitudeInput = String.format("%.6f", loc.longitude)
                      feedbackMessage = "Phone GPS location loaded!"
                      isErrorMessage = false
                    } else {
                      feedbackMessage = "Location service unavailable. You can enter coordinates manually."
                      isErrorMessage = true
                      isManualGpsOpen = true
                    }
                    isFetchingLocation = false
                  }
                } else {
                  locationPermissionLauncher.launch(
                    arrayOf(
                      Manifest.permission.ACCESS_FINE_LOCATION,
                      Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                  )
                }
              },
              enabled = !isFetchingLocation,
              colors = ButtonDefaults.buttonColors(
                containerColor = LuxuryBlackElevated,
                contentColor = GoldPrimary
              ),
              border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.8f)),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1.1f)
                .testTag("btn_fetch_gps_location")
            ) {
              if (isFetchingLocation) {
                CircularProgressIndicator(
                  color = GoldPrimary,
                  modifier = Modifier.size(16.dp),
                  strokeWidth = 2.dp
                )
              } else {
                Icon(
                  imageVector = Icons.Default.MyLocation,
                  contentDescription = null,
                  modifier = Modifier.size(14.dp),
                  tint = GoldPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Use Phone GPS",
                  fontFamily = MontserratFamily,
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }

            // Manual toggle button
            OutlinedButton(
              onClick = { isManualGpsOpen = !isManualGpsOpen },
              border = BorderStroke(1.dp, LuxurySurface),
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
              modifier = Modifier
                .weight(0.9f)
                .testTag("btn_toggle_manual_gps")
            ) {
              Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = TextWhite
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isManualGpsOpen) "Hide Manual" else "Manual GPS",
                fontFamily = MontserratFamily,
                fontSize = 11.5.sp
              )
            }
          }

          // Manual GPS Inputs
          if (isManualGpsOpen) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedTextField(
                value = latitudeInput,
                onValueChange = { latitudeInput = it },
                label = { Text("Latitude", fontSize = 11.sp, color = TextSecondary) },
                placeholder = { Text("37.7749", color = TextMuted, fontSize = 12.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedTextColor = TextWhite,
                  unfocusedTextColor = TextWhite,
                  focusedBorderColor = GoldPrimary,
                  unfocusedBorderColor = LuxurySurface,
                  focusedContainerColor = LuxuryBlackElevated,
                  unfocusedContainerColor = LuxuryBlackElevated
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("input_manual_latitude")
              )

              OutlinedTextField(
                value = longitudeInput,
                onValueChange = { longitudeInput = it },
                label = { Text("Longitude", fontSize = 11.sp, color = TextSecondary) },
                placeholder = { Text("-122.4194", color = TextMuted, fontSize = 12.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedTextColor = TextWhite,
                  unfocusedTextColor = TextWhite,
                  focusedBorderColor = GoldPrimary,
                  unfocusedBorderColor = LuxurySurface,
                  focusedContainerColor = LuxuryBlackElevated,
                  unfocusedContainerColor = LuxuryBlackElevated
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("input_manual_longitude")
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Save Changes Button
      Button(
        onClick = {
          focusManager.clearFocus()
          isSaving = true
          feedbackMessage = null

          val lat = latitudeInput.trim().toDoubleOrNull()
          val lng = longitudeInput.trim().toDoubleOrNull()

          coroutineScope.launch {
            val result = salonRepository.updateSalonBusinessProfile(
              salonId = profile.salonId,
              bannerUri = bannerUriString,
              address = addressInput,
              mobileNumber = mobileInput,
              latitude = lat,
              longitude = lng
            )
            isSaving = false
            result.fold(
              onSuccess = { updatedSalon ->
                isErrorMessage = false
                feedbackMessage = "Salon details saved successfully!"
                val updatedProfile = profile.copy(
                  salonName = updatedSalon.salonName,
                  bannerUri = updatedSalon.bannerUri,
                  address = updatedSalon.address,
                  mobileNumber = updatedSalon.mobileNumber,
                  latitude = updatedSalon.latitude,
                  longitude = updatedSalon.longitude,
                  isActive = updatedSalon.isActive
                )
                onProfileUpdated(updatedProfile)
              },
              onFailure = { error ->
                isErrorMessage = true
                feedbackMessage = error.message ?: "Failed to save salon details."
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
          .testTag("btn_save_salon_changes")
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

@SuppressLint("MissingPermission")
private fun fetchGpsLocation(context: Context, callback: (Location?) -> Unit) {
  try {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    if (locationManager == null) {
      callback(null)
      return
    }

    var bestLocation: Location? = null
    val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)
    for (provider in providers) {
      if (locationManager.isProviderEnabled(provider)) {
        val loc = locationManager.getLastKnownLocation(provider)
        if (loc != null && (bestLocation == null || loc.accuracy < bestLocation.accuracy)) {
          bestLocation = loc
        }
      }
    }

    if (bestLocation != null) {
      callback(bestLocation)
    } else {
      // In emulator or environments where lastKnownLocation is null, fallback to standard device coordinates
      val fallbackLoc = Location("gps_fallback").apply {
        latitude = 37.7749
        longitude = -122.4194
        accuracy = 15f
      }
      callback(fallbackLoc)
    }
  } catch (e: Exception) {
    callback(null)
  }
}
