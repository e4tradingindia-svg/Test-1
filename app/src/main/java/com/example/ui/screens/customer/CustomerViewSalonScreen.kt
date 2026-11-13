package com.example.ui.screens.customer

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.SalonBookingEntity
import com.example.data.model.SalonEntity
import com.example.data.model.SalonServiceEntity
import com.example.data.repository.CustomerRepository
import com.example.data.repository.SalonRepository
import com.example.ui.theme.ActiveGreen
import com.example.ui.theme.ActiveGreenBg
import com.example.ui.theme.BlackBackground
import com.example.ui.theme.CinzelFamily
import com.example.ui.theme.DeactivatedRed
import com.example.ui.theme.DeactivatedRedBg
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CustomerViewSalonScreen(
    salonId: String,
    customerId: String,
    salonRepository: SalonRepository,
    customerRepository: CustomerRepository,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val salon by salonRepository.observeSalonById(salonId).collectAsState(initial = null)
    val services by salonRepository.observeServices(salonId).collectAsState(initial = emptyList())
    val customer by customerRepository.observeCustomer(customerId).collectAsState(initial = null)

    // Full-screen interior photo gallery viewer state
    var selectedFullscreenPhotoIndex by remember { mutableStateOf<Int?>(null) }

    // Booking selections
    val selectedServices = remember { mutableStateListOf<SalonServiceEntity>() }
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displayDateFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())

    val dateOptions = remember {
        (0..6).map { dayOffset ->
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, dayOffset) }
            val formattedIso = dateFormat.format(cal.time)
            val formattedDisplay = if (dayOffset == 0) "Today" else if (dayOffset == 1) "Tomorrow" else displayDateFormat.format(cal.time)
            formattedIso to formattedDisplay
        }
    }

    var selectedDateIso by remember { mutableStateOf(dateOptions.first().first) }
    var selectedTimeSlot by remember { mutableStateOf("11:00 AM") }
    var existingBookingsForDate by remember { mutableStateOf<List<SalonBookingEntity>>(emptyList()) }
    var isBookingConfirmed by remember { mutableStateOf(false) }
    var isSubmittingBooking by remember { mutableStateOf(false) }

    val availableSlots = listOf(
        "09:30 AM", "10:30 AM", "11:30 AM",
        "01:00 PM", "02:30 PM", "04:00 PM",
        "05:30 PM", "07:00 PM"
    )

    // Refresh existing bookings whenever date changes
    LaunchedEffect(salonId, selectedDateIso) {
        existingBookingsForDate = salonRepository.getBookingsForDate(salonId, selectedDateIso)
    }

    val interiorPhotos = remember(salon?.interiorPhotoUris) {
        salon?.getInteriorPhotoList() ?: emptyList()
    }

    val totalPrice = selectedServices.sumOf { it.price }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = salon?.salonName ?: "Salon Details",
                        fontFamily = CinzelFamily,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        fontSize = 18.sp,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("btn_view_salon_back")
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
        if (salon == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GoldPrimary)
            }
        } else {
            val currentSalon = salon!!

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main Salon Banner / Poster
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                    ) {
                        if (currentSalon.bannerUri.isNotBlank()) {
                            AsyncImage(
                                model = currentSalon.bannerUri,
                                contentDescription = "${currentSalon.salonName} Banner",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            val fallbackRes = when (currentSalon.businessType) {
                                "Men's Salon" -> R.drawable.salonmate_men_hero
                                "Women's Beauty Parlour" -> R.drawable.salonmate_women_hero
                                else -> R.drawable.hero_salon_banner
                            }
                            Image(
                                painter = painterResource(id = fallbackRes),
                                contentDescription = "Default Salon Banner",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Gradient overlay at bottom
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, BlackBackground),
                                        startY = 100f
                                    )
                                )
                        )

                        // Status Badge (Active / Deactivated)
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (currentSalon.isActive) ActiveGreenBg else DeactivatedRedBg)
                                .border(
                                    1.dp,
                                    if (currentSalon.isActive) ActiveGreen else DeactivatedRed,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (currentSalon.isActive) "ACTIVE" else "DEACTIVATED",
                                color = if (currentSalon.isActive) ActiveGreen else DeactivatedRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Interior Photo Gallery Section (Requirement: directly below main banner)
                // Show ONLY if photos were uploaded by owner. If empty, hide completely.
                if (interiorPhotos.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .testTag("section_interior_photo_gallery")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Salon Interior Gallery",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                )
                                Text(
                                    text = "Tap photo to zoom",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 3-4 Thumbnail Boxes directly below main banner
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                interiorPhotos.forEachIndexed { index, uriStr ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(72.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(LuxuryCardBg)
                                            .border(1.dp, GoldBorder, RoundedCornerShape(10.dp))
                                            .clickable {
                                                selectedFullscreenPhotoIndex = index
                                            }
                                            .testTag("interior_thumbnail_$index"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = uriStr,
                                            contentDescription = "Interior photo ${index + 1}",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Salon Info & GPS Directions Card
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(LuxuryCardBg)
                            .border(1.dp, GoldBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = currentSalon.salonName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = CinzelFamily,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PinkPrimary.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = currentSalon.businessType,
                                    color = PinkPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${currentSalon.rating} (${currentSalon.reviewCount} reviews)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = GoldLight,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = PinkPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = currentSalon.address,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // GPS Get Directions Button (Opens Google Maps)
                        Button(
                            onClick = {
                                val mapIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("geo:${currentSalon.latitude},${currentSalon.longitude}?q=${currentSalon.latitude},${currentSalon.longitude}(${Uri.encode(currentSalon.salonName)})")
                                ).apply {
                                    setPackage("com.google.android.apps.maps")
                                }
                                try {
                                    context.startActivity(mapIntent)
                                } catch (e: Exception) {
                                    // Fallback to web browser maps
                                    val webMap = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://www.google.com/maps/search/?api=1&query=${currentSalon.latitude},${currentSalon.longitude}")
                                    )
                                    context.startActivity(webMap)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("btn_get_directions"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LuxurySurface,
                                contentColor = GoldPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Directions,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Get GPS Directions (Google Maps)",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Deactivated Warning if not active
                if (!currentSalon.isActive) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DeactivatedRedBg)
                                .border(1.dp, DeactivatedRed, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = DeactivatedRed,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "This salon is currently DEACTIVATED by the owner and is not accepting new appointments.",
                                    color = DeactivatedRed,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Service Selection Section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Select Services",
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = CinzelFamily,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                        Text(
                            text = "Choose one or more services for your appointment",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                        )

                        services.forEach { service ->
                            val isSelected = selectedServices.any { it.serviceId == service.serviceId }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable(enabled = currentSalon.isActive) {
                                        if (isSelected) {
                                            selectedServices.removeAll { it.serviceId == service.serviceId }
                                        } else {
                                            selectedServices.add(service)
                                        }
                                    }
                                    .testTag("service_item_${service.serviceId}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) LuxuryCardBg else LuxurySurface
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) GoldPrimary else GoldBorder.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = { checked ->
                                                if (checked) {
                                                    selectedServices.add(service)
                                                } else {
                                                    selectedServices.removeAll { it.serviceId == service.serviceId }
                                                }
                                            },
                                            enabled = currentSalon.isActive,
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = PinkPrimary,
                                                uncheckedColor = GoldBorder,
                                                checkmarkColor = TextPrimary
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = service.serviceName,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "${service.category} • ${service.durationMinutes} mins",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    Text(
                                        text = "₹${service.price.toInt()}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldLight
                                    )
                                }
                            }
                        }
                    }
                }

                // Date Selection
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Select Date",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(dateOptions) { (isoDate, label) ->
                                val isSelected = selectedDateIso == isoDate
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) GoldPrimary else LuxuryCardBg)
                                        .border(
                                            1.dp,
                                            if (isSelected) GoldPrimary else GoldBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable(enabled = currentSalon.isActive) {
                                            selectedDateIso = isoDate
                                        }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                        .testTag("date_chip_$isoDate")
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) BlackBackground else TextPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Time Slots (Double booking prevented)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Available Time Slots",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Slots Grid
                        val bookedSlots = existingBookingsForDate.map { it.timeSlot }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableSlots.take(4).forEach { slot ->
                                val isBooked = bookedSlots.any { it.equals(slot, ignoreCase = true) }
                                val isSelected = selectedTimeSlot == slot
                                SlotButton(
                                    slot = slot,
                                    isBooked = isBooked,
                                    isSelected = isSelected && !isBooked,
                                    enabled = currentSalon.isActive && !isBooked,
                                    modifier = Modifier.weight(1f),
                                    onSelect = { selectedTimeSlot = slot }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableSlots.drop(4).forEach { slot ->
                                val isBooked = bookedSlots.any { it.equals(slot, ignoreCase = true) }
                                val isSelected = selectedTimeSlot == slot
                                SlotButton(
                                    slot = slot,
                                    isBooked = isBooked,
                                    isSelected = isSelected && !isBooked,
                                    enabled = currentSalon.isActive && !isBooked,
                                    modifier = Modifier.weight(1f),
                                    onSelect = { selectedTimeSlot = slot }
                                )
                            }
                        }
                    }
                }

                // Booking Summary Card
                if (selectedServices.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(LuxuryCardBg)
                                .border(1.dp, GoldBorder, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Booking Summary",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            selectedServices.forEach { s ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = s.serviceName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "₹${s.price.toInt()}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(GoldBorder.copy(alpha = 0.4f))
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Amount",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "₹${totalPrice.toInt()}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                            }

                            Text(
                                text = "Appointment Date & Time: $selectedDateIso at $selectedTimeSlot",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Confirm Booking Action Button
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Button(
                            onClick = {
                                if (selectedServices.isEmpty()) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Please select at least one service")
                                    }
                                    return@Button
                                }
                                if (!currentSalon.isActive) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Cannot book: This salon is currently DEACTIVATED")
                                    }
                                    return@Button
                                }
                                isSubmittingBooking = true
                                coroutineScope.launch {
                                    val result = salonRepository.createBooking(
                                        salonId = salonId,
                                        customerId = customerId,
                                        customerName = customer?.name ?: "Customer",
                                        customerPhone = customer?.mobileNumber ?: "",
                                        serviceIds = selectedServices.map { it.serviceId },
                                        serviceNames = selectedServices.map { it.serviceName },
                                        bookingDate = selectedDateIso,
                                        timeSlot = selectedTimeSlot,
                                        totalAmount = totalPrice
                                    )
                                    isSubmittingBooking = false
                                    result.onSuccess {
                                        isBookingConfirmed = true
                                        // Refresh bookings
                                        existingBookingsForDate = salonRepository.getBookingsForDate(salonId, selectedDateIso)
                                    }.onFailure { ex ->
                                        snackbarHostState.showSnackbar(ex.message ?: "Booking failed")
                                    }
                                }
                            },
                            enabled = currentSalon.isActive && selectedServices.isNotEmpty() && !isSubmittingBooking,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("btn_confirm_booking"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PinkPrimary,
                                disabledContainerColor = LuxuryCardBg
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isSubmittingBooking) {
                                CircularProgressIndicator(color = TextPrimary, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = if (!currentSalon.isActive) "Salon Deactivated (Unavailable)"
                                    else if (selectedServices.isEmpty()) "Select Service to Book"
                                    else "Confirm Booking (₹${totalPrice.toInt()})",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentSalon.isActive && selectedServices.isNotEmpty()) TextPrimary else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Booking Confirmation Dialog
        if (isBookingConfirmed) {
            Dialog(onDismissRequest = { isBookingConfirmed = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(LuxuryCardBg)
                        .border(1.5.dp, GoldPrimary, RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(ActiveGreenBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = ActiveGreen,
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Booking Confirmed!",
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = CinzelFamily,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Your appointment is booked for $selectedDateIso at $selectedTimeSlot. The salon owner has been notified.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                isBookingConfirmed = false
                                selectedServices.clear()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_dismiss_booking_success"),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                        ) {
                            Text(
                                text = "Done",
                                color = BlackBackground,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // FULLSCREEN INTERIOR PHOTO VIEWER DIALOG / OVERLAY
        // Requirement 4: "When a customer taps any thumbnail, open that photo in a large, clear, full-screen gallery viewer. Allow swiping between photos and closing the viewer."
        if (selectedFullscreenPhotoIndex != null && interiorPhotos.isNotEmpty()) {
            val initialIndex = selectedFullscreenPhotoIndex!!.coerceIn(0, interiorPhotos.size - 1)
            val pagerState = rememberPagerState(
                initialPage = initialIndex,
                pageCount = { interiorPhotos.size }
            )

            BackHandler {
                selectedFullscreenPhotoIndex = null
            }

            Dialog(
                onDismissRequest = { selectedFullscreenPhotoIndex = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xF5050505))
                        .testTag("fullscreen_gallery_viewer")
                ) {
                    // Top App Bar inside full screen viewer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Salon Interior",
                                fontFamily = CinzelFamily,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Photo ${pagerState.currentPage + 1} of ${interiorPhotos.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldLight
                            )
                        }

                        // Close button
                        IconButton(
                            onClick = { selectedFullscreenPhotoIndex = null },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(LuxuryCardBg)
                                .border(1.dp, GoldBorder, CircleShape)
                                .testTag("btn_close_gallery_viewer")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Gallery",
                                tint = GoldPrimary
                            )
                        }
                    }

                    // Large clear swipeable photos
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize()
                            .padding(vertical = 80.dp)
                            .testTag("gallery_horizontal_pager")
                    ) { page ->
                        val photoUri = interiorPhotos[page]
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = photoUri,
                                contentDescription = "Full interior view $page",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, GoldBorder, RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    // Bottom Navigation Dots / Counter
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        interiorPhotos.indices.forEach { index ->
                            val isCurrent = pagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .size(if (isCurrent) 10.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(if (isCurrent) GoldPrimary else GoldBorder)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SlotButton(
    slot: String,
    isBooked: Boolean,
    isSelected: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isBooked) DeactivatedRedBg
                else if (isSelected) GoldPrimary
                else LuxurySurface
            )
            .border(
                1.dp,
                if (isBooked) DeactivatedRed.copy(alpha = 0.5f)
                else if (isSelected) GoldPrimary
                else GoldBorder.copy(alpha = 0.4f),
                RoundedCornerShape(8.dp)
            )
            .clickable(enabled = enabled && !isBooked, onClick = onSelect)
            .padding(vertical = 10.dp)
            .testTag("slot_button_${slot.replace(" ", "_")}"),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = slot,
                color = if (isBooked) DeactivatedRed
                else if (isSelected) BlackBackground
                else TextPrimary,
                fontSize = 11.sp,
                fontWeight = if (isSelected || isBooked) FontWeight.Bold else FontWeight.Normal
            )
            if (isBooked) {
                Text(
                    text = "Reserved",
                    color = DeactivatedRed,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
