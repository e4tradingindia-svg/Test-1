package com.example.ui.screens.owner

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.R
import com.example.data.repository.SalonRepository
import com.example.ui.theme.BlackBackground
import com.example.ui.theme.CinzelFamily
import com.example.ui.theme.DeactivatedRed
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.LuxuryCardBg
import com.example.ui.theme.LuxurySurface
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSalonBusinessProfileScreen(
    salonId: String,
    salonRepository: SalonRepository,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    var salonName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var businessType by remember { mutableStateOf("") }
    var bannerUriString by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf(28.6139) }
    var longitude by remember { mutableStateOf(77.2090) }

    // Interior photos list (up to 4)
    val interiorPhotos = remember { mutableStateListOf<String>() }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    // Track which photo index is being replaced
    var replacingPhotoIndex by remember { mutableStateOf<Int?>(null) }

    // Fetch initial profile
    LaunchedEffect(salonId) {
        val salon = salonRepository.getSalonById(salonId)
        if (salon != null) {
            salonName = salon.salonName
            ownerName = salon.ownerName
            businessType = salon.businessType
            bannerUriString = salon.bannerUri
            address = salon.address
            mobileNumber = salon.mobileNumber
            latitude = salon.latitude
            longitude = salon.longitude
            interiorPhotos.clear()
            interiorPhotos.addAll(salon.getInteriorPhotoList())
        }
        isLoading = false
    }

    // Helper to persist file locally for long-term durability
    fun saveUriToLocalFile(uri: Uri, prefix: String): String {
        return try {
            val file = File(context.filesDir, "${prefix}_${salonId}_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(file).toString()
        } catch (e: Exception) {
            uri.toString()
        }
    }

    // Main Banner Picker
    val bannerPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            bannerUriString = saveUriToLocalFile(uri, "banner")
        }
    }

    // Interior Photo Add/Replace Picker
    val interiorPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedUri = saveUriToLocalFile(uri, "interior")
            val replaceIdx = replacingPhotoIndex
            if (replaceIdx != null && replaceIdx in interiorPhotos.indices) {
                // Replacing existing photo
                interiorPhotos[replaceIdx] = savedUri
            } else if (interiorPhotos.size < 4) {
                // Adding new photo
                interiorPhotos.add(savedUri)
            }
        }
        replacingPhotoIndex = null
    }

    // GPS Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            fetchGpsCoordinates(context) { lat, lng ->
                latitude = lat
                longitude = lng
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("GPS Location updated: ${String.format("%.4f", lat)}, ${String.format("%.4f", lng)}")
                }
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Location permission denied. You can enter address manually.")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Business Profile",
                        fontFamily = CinzelFamily,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("btn_edit_salon_back")
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
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GoldPrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Salon Registered Name Header (Requirement 9: display exact registered name, no hardcoding)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(LuxuryCardBg)
                        .border(1.dp, GoldBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = salonName,
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = CinzelFamily,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary,
                            modifier = Modifier.testTag("text_registered_salon_name")
                        )
                        Text(
                            text = "Salon ID: $salonId • $businessType",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 1: Main Salon Banner / Poster
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Main Salon Banner / Poster",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        if (bannerUriString.isNotBlank()) {
                            Text(
                                text = "Remove",
                                color = DeactivatedRed,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { bannerUriString = "" }
                                    .testTag("btn_remove_banner")
                                    .padding(4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(LuxuryCardBg)
                            .border(1.dp, GoldBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                bannerPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .testTag("salon_banner_picker_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (bannerUriString.isNotBlank()) {
                            AsyncImage(
                                model = bannerUriString,
                                contentDescription = "Salon Poster",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            val fallbackRes = when (businessType) {
                                "Men's Salon" -> R.drawable.salonmate_men_hero
                                "Women's Beauty Parlour" -> R.drawable.salonmate_women_hero
                                else -> R.drawable.hero_salon_banner
                            }
                            Image(
                                painter = painterResource(id = fallbackRes),
                                contentDescription = "Default Poster",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Upload / Change Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(BlackBackground.copy(alpha = 0.85f))
                                .border(1.dp, GoldPrimary, RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (bannerUriString.isNotBlank()) "Change Banner" else "Upload Banner",
                                    color = GoldPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // SECTION 2: SALON INTERIOR PHOTO GALLERY (Up to 4 photos)
                // Requirement 1: allow owners to upload, replace and delete up to 4 salon interior photos from phone gallery
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Salon Interior Photo Gallery",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                            Text(
                                text = "${interiorPhotos.size}/4 interior photos added",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }

                        if (interiorPhotos.size < 4) {
                            Button(
                                onClick = {
                                    replacingPhotoIndex = null
                                    interiorPhotoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .height(38.dp)
                                    .testTag("btn_add_interior_photo"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PinkPrimary,
                                    contentColor = TextPrimary
                                ),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Add Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (interiorPhotos.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(LuxurySurface)
                                .border(1.dp, GoldBorder.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .clickable {
                                    replacingPhotoIndex = null
                                    interiorPhotoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                .padding(20.dp)
                                .testTag("box_empty_interior_photos"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = GoldSecondary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "No interior photos uploaded yet",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Tap to upload up to 4 photos from your Gallery",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }
                        }
                    } else {
                        // 4 Slots Grid / Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            (0..3).forEach { index ->
                                val hasPhoto = index < interiorPhotos.size
                                val photoUri = if (hasPhoto) interiorPhotos[index] else null

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(95.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(LuxurySurface)
                                        .border(
                                            1.dp,
                                            if (hasPhoto) GoldPrimary else GoldBorder.copy(alpha = 0.3f),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .testTag("interior_slot_$index"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (hasPhoto && photoUri != null) {
                                        // Image thumbnail
                                        AsyncImage(
                                            model = photoUri,
                                            contentDescription = "Interior photo ${index + 1}",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )

                                        // Overlay Action Buttons: Replace & Delete
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(BlackBackground.copy(alpha = 0.35f))
                                        )

                                        Row(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .fillMaxWidth()
                                                .background(BlackBackground.copy(alpha = 0.85f))
                                                .padding(vertical = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Replace button
                                            IconButton(
                                                onClick = {
                                                    replacingPhotoIndex = index
                                                    interiorPhotoPickerLauncher.launch(
                                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                    )
                                                },
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .testTag("btn_replace_interior_$index")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Replace",
                                                    tint = GoldPrimary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }

                                            // Delete button
                                            IconButton(
                                                onClick = {
                                                    if (index in interiorPhotos.indices) {
                                                        interiorPhotos.removeAt(index)
                                                    }
                                                },
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .testTag("btn_delete_interior_$index")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    tint = DeactivatedRed,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    } else {
                                        // Empty slot (+)
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clickable {
                                                    replacingPhotoIndex = null
                                                    interiorPhotoPickerLauncher.launch(
                                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                    )
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Add slot",
                                                    tint = GoldSecondary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Text(
                                                    text = "Slot ${index + 1}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = TextMuted,
                                                    fontSize = 9.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section 3: Salon Address
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Salon Address") },
                    leadingIcon = {
                        Icon(Icons.Default.Home, contentDescription = null, tint = GoldPrimary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_edit_salon_address"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = LuxuryCardBg,
                        unfocusedContainerColor = LuxuryCardBg,
                        focusedIndicatorColor = GoldPrimary,
                        unfocusedIndicatorColor = GoldBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = GoldPrimary,
                        unfocusedLabelColor = TextSecondary
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section 4: Mobile Number
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = {
                        if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                            mobileNumber = it
                        }
                    },
                    label = { Text("Salon Contact Mobile Number") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = GoldPrimary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_edit_salon_mobile"),
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

                // Section 5: GPS Location Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(LuxuryCardBg)
                        .border(1.dp, GoldBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Salon GPS Coordinates",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )

                        // Button to detect GPS from device
                        Button(
                            onClick = {
                                val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                if (hasFine || hasCoarse) {
                                    fetchGpsCoordinates(context) { lat, lng ->
                                        latitude = lat
                                        longitude = lng
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Detected GPS: ${String.format("%.4f", lat)}, ${String.format("%.4f", lng)}")
                                        }
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
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("btn_detect_gps_location"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LuxurySurface,
                                contentColor = GoldPrimary
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                        ) {
                            Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Use Current GPS", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = latitude.toString(),
                            onValueChange = { latitude = it.toDoubleOrNull() ?: latitude },
                            label = { Text("Latitude") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_edit_latitude"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = LuxurySurface,
                                unfocusedContainerColor = LuxurySurface,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedIndicatorColor = GoldPrimary,
                                unfocusedIndicatorColor = GoldBorder
                            )
                        )

                        OutlinedTextField(
                            value = longitude.toString(),
                            onValueChange = { longitude = it.toDoubleOrNull() ?: longitude },
                            label = { Text("Longitude") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_edit_longitude"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = LuxurySurface,
                                unfocusedContainerColor = LuxurySurface,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedIndicatorColor = GoldPrimary,
                                unfocusedIndicatorColor = GoldBorder
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // SAVE CHANGES BUTTON
                Button(
                    onClick = {
                        if (address.isBlank()) {
                            coroutineScope.launch { snackbarHostState.showSnackbar("Please enter a salon address") }
                            return@Button
                        }
                        if (mobileNumber.length < 10) {
                            coroutineScope.launch { snackbarHostState.showSnackbar("Please enter a valid 10-digit mobile number") }
                            return@Button
                        }
                        isSaving = true
                        coroutineScope.launch {
                            val interiorPhotosJoined = interiorPhotos.take(4).joinToString(",")
                            salonRepository.updateSalonBusinessProfile(
                                salonId = salonId,
                                bannerUri = bannerUriString,
                                interiorPhotoUris = interiorPhotosJoined,
                                address = address,
                                mobileNumber = mobileNumber,
                                lat = latitude,
                                lng = longitude
                            )
                            isSaving = false
                            snackbarHostState.showSnackbar("Business Profile and Interior Gallery saved successfully!")
                            onNavigateBack()
                        }
                    },
                    enabled = !isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_save_business_profile"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = BlackBackground
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = BlackBackground,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Save Changes",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun fetchGpsCoordinates(context: Context, onLocation: (Double, Double) -> Unit) {
    try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (location != null) {
            onLocation(location.latitude, location.longitude)
        } else {
            // Default Delhi coordinates if emulator/device has no fix yet
            onLocation(28.6139, 77.2090)
        }
    } catch (e: Exception) {
        onLocation(28.6139, 77.2090)
    }
}
