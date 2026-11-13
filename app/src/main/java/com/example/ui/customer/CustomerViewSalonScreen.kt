package com.example.ui.customer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CustomerEntity
import com.example.data.model.SalonBookingEntity
import com.example.data.model.SalonEntity
import com.example.data.model.SalonServiceEntity
import com.example.data.model.SalonStaffEntity
import com.example.data.repository.SalonRepository
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
import kotlinx.coroutines.launch
import java.io.File

data class BookingDateOption(
  val isoDate: String,
  val dayLabel: String,
  val dateLabel: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomerViewSalonScreen(
  salon: SalonEntity,
  customer: CustomerEntity,
  salonRepository: SalonRepository,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  // Real database services & staff
  val services by salonRepository.observeServicesForSalon(salon.salonId).collectAsState(initial = emptyList())
  val staffList by salonRepository.observeStaffForSalon(salon.salonId).collectAsState(initial = emptyList())

  // Booking selections
  var selectedServices by remember { mutableStateOf<Set<SalonServiceEntity>>(emptySet()) }
  val availableDates = remember {
    listOf(
      BookingDateOption("2026-11-09", "Today", "Nov 9"),
      BookingDateOption("2026-11-10", "Tomorrow", "Nov 10"),
      BookingDateOption("2026-11-11", "Wed", "Nov 11"),
      BookingDateOption("2026-11-12", "Thu", "Nov 12"),
      BookingDateOption("2026-11-13", "Fri", "Nov 13"),
      BookingDateOption("2026-11-14", "Sat", "Nov 14"),
      BookingDateOption("2026-11-15", "Sun", "Nov 15")
    )
  }
  var selectedDate by remember { mutableStateOf(availableDates.first().isoDate) }

  // Real database bookings for selected date (slot availability & double-booking prevention)
  val dateBookings by salonRepository.observeBookingsForDate(salon.salonId, selectedDate).collectAsState(initial = emptyList())
  val reservedSlots = remember(dateBookings) { dateBookings.map { it.timeSlot }.toSet() }

  val standardTimeSlots = remember {
    listOf(
      "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM",
      "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM",
      "01:00 PM", "01:30 PM", "02:00 PM", "02:30 PM",
      "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM",
      "05:00 PM", "05:30 PM", "06:00 PM", "06:30 PM",
      "07:00 PM", "07:30 PM"
    )
  }
  var selectedTimeSlot by remember { mutableStateOf<String?>(null) }
  var selectedStaff by remember { mutableStateOf<SalonStaffEntity?>(null) }

  var isConfirming by remember { mutableStateOf(false) }
  var bookingErrorMessage by remember { mutableStateOf<String?>(null) }
  var confirmedBooking by remember { mutableStateOf<SalonBookingEntity?>(null) }

  // Total calculations
  val totalPrice = selectedServices.sumOf { it.price }
  val totalDuration = selectedServices.sumOf { it.durationMinutes }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(LuxuryBlack)
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("customer_view_salon_root"),
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
            .testTag("view_salon_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back to Portal",
            tint = GoldPrimary,
            modifier = Modifier.size(20.dp)
          )
        }

        Text(
          text = stringResource(R.string.customer_salon_details_title),
          fontFamily = MontserratFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = TextWhite
        )

        Spacer(modifier = Modifier.size(42.dp))
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Salon Banner Image Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .height(190.dp)
          .testTag("view_salon_banner_card"),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, GoldBorder)
      ) {
        val bannerUri = salon.bannerUri
        var bannerBitmap by remember(bannerUri) {
          mutableStateOf<android.graphics.Bitmap?>(null)
        }

        if (bannerUri != null) {
          remember(bannerUri) {
            try {
              val parsed = Uri.parse(bannerUri)
              if (parsed.scheme == "file") {
                val f = File(parsed.path ?: "")
                if (f.exists()) {
                  bannerBitmap = BitmapFactory.decodeFile(f.absolutePath)
                }
              } else {
                context.contentResolver.openInputStream(parsed)?.use { s ->
                  bannerBitmap = BitmapFactory.decodeStream(s)
                }
              }
            } catch (e: Exception) {
              bannerBitmap = null
            }
            true
          }
        }

        val bBitmap = bannerBitmap
        if (bBitmap != null) {
          Image(
            bitmap = bBitmap.asImageBitmap(),
            contentDescription = "Salon Banner",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        } else {
          Image(
            painter = painterResource(id = R.drawable.hero_salon_banner),
            contentDescription = "Default Salon Banner",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Salon Profile Overview Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("view_salon_info_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CustomerCardBg),
        border = BorderStroke(1.dp, PinkBorder)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          // Salon Name & Rating Badge
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = salon.salonName,
                fontFamily = MontserratFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextWhite,
                modifier = Modifier.testTag("view_salon_name")
              )
              Text(
                text = salon.businessType,
                fontFamily = MontserratFamily,
                fontSize = 13.sp,
                color = PinkPrimary
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(GoldTagBg)
                .border(0.5.dp, GoldBorder, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Star,
                  contentDescription = null,
                  tint = GoldPrimary,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "${salon.rating} (${salon.reviewCount})",
                  fontFamily = MontserratFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = GoldPrimary
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Horizontal Active / Deactivated Partner Status Banner
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(if (salon.isActive) Color(0xFF0C2414) else Color(0xFF2B0A0D))
              .border(
                1.dp,
                if (salon.isActive) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                RoundedCornerShape(8.dp)
              )
              .padding(horizontal = 12.dp, vertical = 9.dp)
              .testTag("view_salon_status_banner")
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(if (salon.isActive) Color(0xFF1B4D27) else Color(0xFF4A1217)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (salon.isActive) Icons.Default.CheckCircle else Icons.Default.Cancel,
                  contentDescription = null,
                  tint = if (salon.isActive) Color(0xFF4CAF50) else Color(0xFFFF5252),
                  modifier = Modifier.size(15.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "Partner Status:",
                    fontFamily = MontserratFamily,
                    fontSize = 11.5.sp,
                    color = TextSecondary
                  )
                  Spacer(modifier = Modifier.width(5.dp))
                  Text(
                    text = if (salon.isActive) "ACTIVE" else "DEACTIVATED",
                    fontFamily = MontserratFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (salon.isActive) Color(0xFF4CAF50) else Color(0xFFFF5252),
                    modifier = Modifier.testTag("view_salon_status_text")
                  )
                }
                Text(
                  text = if (salon.isActive)
                    "Online • Accepting customer bookings & orders"
                  else
                    "Offline • Currently unavailable for new bookings/orders",
                  fontFamily = MontserratFamily,
                  fontSize = 10.5.sp,
                  color = if (salon.isActive) Color(0xFFA5D6A7) else Color(0xFFFFB4C0)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Address Row
          Row(verticalAlignment = Alignment.Top) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = GoldPrimary,
              modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Salon Address",
                fontFamily = MontserratFamily,
                fontSize = 11.sp,
                color = TextSecondary
              )
              Text(
                text = if (!salon.address.isNullOrBlank()) salon.address else "Address provided on booking",
                fontFamily = MontserratFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 13.5.sp,
                color = TextIvory,
                modifier = Modifier.testTag("view_salon_address")
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Phone Number Row
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Phone,
              contentDescription = null,
              tint = GoldPrimary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Contact Number",
                fontFamily = MontserratFamily,
                fontSize = 11.sp,
                color = TextSecondary
              )
              Text(
                text = salon.mobileNumber,
                fontFamily = MontserratFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.5.sp,
                color = TextWhite,
                modifier = Modifier.testTag("view_salon_phone")
              )
            }
          }

          // GPS Coordinates if present
          if (salon.latitude != null && salon.longitude != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Directions,
                contentDescription = null,
                tint = GoldAccent,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "GPS Coordinates: ${salon.latitude}, ${salon.longitude}",
                fontFamily = MontserratFamily,
                fontSize = 11.5.sp,
                color = GoldAccent,
                modifier = Modifier.testTag("view_salon_gps")
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Directions & Call Quick Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = { openGoogleMapsDirections(context, salon) },
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .height(44.dp)
            .background(
              Brush.horizontalGradient(listOf(GoldGradientStart, GoldPrimary, GoldAccent)),
              RoundedCornerShape(12.dp)
            )
            .testTag("btn_get_directions")
        ) {
          Icon(
            imageVector = Icons.Default.Directions,
            contentDescription = null,
            tint = Color(0xFF1E1302),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = stringResource(R.string.customer_btn_get_directions),
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.5.sp,
            color = Color(0xFF1E1302)
          )
        }

        OutlinedButton(
          onClick = {
            val callIntent = Intent(Intent.ACTION_DIAL).apply {
              data = Uri.parse("tel:${salon.mobileNumber.filter { it.isDigit() || it == '+' }}")
            }
            context.startActivity(callIntent)
          },
          border = BorderStroke(1.dp, GoldBorder),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
          modifier = Modifier
            .weight(0.9f)
            .height(44.dp)
            .testTag("btn_call_salon")
        ) {
          Icon(
            imageVector = Icons.Default.Call,
            contentDescription = null,
            tint = GoldPrimary,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = stringResource(R.string.customer_btn_call_salon),
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.5.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // ─────────────────────────────────────────────────────────────
      // 1. SELECT SERVICES & AUTOMATIC TOTAL PRICE CALCULATION
      // ─────────────────────────────────────────────────────────────
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "SELECT SERVICES",
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = GoldPrimary,
            letterSpacing = 1.sp
          )
          Text(
            text = "From verified salon service menu",
            fontFamily = MontserratFamily,
            fontSize = 11.sp,
            color = TextSecondary
          )
        }

        // Live Selected Count & Total
        if (selectedServices.isNotEmpty()) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(GoldTagBg)
              .border(0.5.dp, GoldBorder, RoundedCornerShape(8.dp))
              .padding(horizontal = 10.dp, vertical = 5.dp)
          ) {
            Text(
              text = "${selectedServices.size} selected • $${String.format("%.2f", totalPrice)}",
              fontFamily = MontserratFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = GoldPrimary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Services List from Room Database
      if (services.isEmpty()) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = LuxurySurface)
        ) {
          Text(
            text = "Loading salon services...",
            fontFamily = MontserratFamily,
            fontSize = 12.sp,
            color = TextMuted,
            modifier = Modifier.padding(16.dp)
          )
        }
      } else {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          services.forEach { service ->
            val isSelected = selectedServices.contains(service)
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  if (salon.isActive) {
                    selectedServices = if (isSelected) {
                      selectedServices - service
                    } else {
                      selectedServices + service
                    }
                    bookingErrorMessage = null
                  }
                }
                .testTag("service_item_${service.serviceId}"),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isSelected) Color(0xFF241C0A) else LuxurySurface
              ),
              border = BorderStroke(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) GoldPrimary else GoldBorder.copy(alpha = 0.35f)
              )
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                // Checkbox indicator
                Box(
                  modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) GoldPrimary else LuxuryBlackElevated)
                    .border(
                      1.dp,
                      if (isSelected) GoldPrimary else GoldBorder.copy(alpha = 0.6f),
                      RoundedCornerShape(6.dp)
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  if (isSelected) {
                    Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = "Selected",
                      tint = Color(0xFF1E1302),
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = service.name,
                      fontFamily = MontserratFamily,
                      fontWeight = FontWeight.SemiBold,
                      fontSize = 13.5.sp,
                      color = if (isSelected) GoldAccent else TextWhite
                    )
                    Text(
                      text = "$${String.format("%.2f", service.price)}",
                      fontFamily = MontserratFamily,
                      fontWeight = FontWeight.Bold,
                      fontSize = 14.sp,
                      color = GoldPrimary,
                      modifier = Modifier.testTag("service_price_${service.serviceId}")
                    )
                  }

                  Spacer(modifier = Modifier.height(4.dp))

                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.AccessTime,
                      contentDescription = null,
                      tint = TextSecondary,
                      modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "${service.durationMinutes} mins • ${service.category}",
                      fontFamily = MontserratFamily,
                      fontSize = 11.sp,
                      color = TextSecondary
                    )
                  }

                  if (service.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = service.description,
                      fontFamily = MontserratFamily,
                      fontSize = 11.sp,
                      color = TextMuted,
                      maxLines = 2
                    )
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // ─────────────────────────────────────────────────────────────
      // 2. DATE SELECTION
      // ─────────────────────────────────────────────────────────────
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "SELECT APPOINTMENT DATE",
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = GoldPrimary,
            letterSpacing = 1.sp
          )
          Text(
            text = "Business hours: 9:00 AM - 8:00 PM",
            fontFamily = MontserratFamily,
            fontSize = 11.sp,
            color = TextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Horizontal Date Selector
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        availableDates.forEach { dateOption ->
          val isDateSelected = selectedDate == dateOption.isoDate
          Box(
            modifier = Modifier
              .width(84.dp)
              .height(64.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isDateSelected) GoldPrimary else LuxurySurface)
              .border(
                1.dp,
                if (isDateSelected) GoldPrimary else GoldBorder.copy(alpha = 0.4f),
                RoundedCornerShape(12.dp)
              )
              .clickable {
                selectedDate = dateOption.isoDate
                bookingErrorMessage = null
              }
              .padding(8.dp)
              .testTag("date_option_${dateOption.isoDate}"),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = dateOption.dayLabel,
                fontFamily = MontserratFamily,
                fontWeight = if (isDateSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp,
                color = if (isDateSelected) Color(0xFF1E1302) else TextSecondary
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = dateOption.dateLabel,
                fontFamily = MontserratFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isDateSelected) Color(0xFF1E1302) else TextWhite
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      // ─────────────────────────────────────────────────────────────
      // 3. AVAILABLE TIME SLOTS (WITH DOUBLE-BOOKING PREVENTION)
      // ─────────────────────────────────────────────────────────────
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "SELECT TIME SLOT",
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = GoldPrimary,
            letterSpacing = 1.sp
          )
          Text(
            text = "Real-time reservation check",
            fontFamily = MontserratFamily,
            fontSize = 11.sp,
            color = TextSecondary
          )
        }

        // Legend: Available vs Reserved
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(GoldPrimary))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Open", fontSize = 10.sp, color = TextSecondary)
          Spacer(modifier = Modifier.width(8.dp))
          Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF552222)))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Reserved", fontSize = 10.sp, color = Color(0xFFFF8FA3))
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Time Slots Flow Grid
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        standardTimeSlots.forEach { slot ->
          val isReserved = reservedSlots.contains(slot)
          val isSlotSelected = selectedTimeSlot == slot

          Box(
            modifier = Modifier
              .width(96.dp)
              .height(42.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(
                when {
                  isReserved -> Color(0xFF261013)
                  isSlotSelected -> GoldPrimary
                  else -> LuxuryBlackElevated
                }
              )
              .border(
                1.dp,
                when {
                  isReserved -> Color(0xFF88222A)
                  isSlotSelected -> GoldPrimary
                  else -> GoldBorder.copy(alpha = 0.4f)
                },
                RoundedCornerShape(8.dp)
              )
              .clickable(enabled = !isReserved && salon.isActive) {
                selectedTimeSlot = slot
                bookingErrorMessage = null
              }
              .testTag(if (isReserved) "slot_reserved_${slot}" else "slot_available_${slot}"),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = slot,
                fontFamily = MontserratFamily,
                fontWeight = if (isSlotSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.5.sp,
                color = when {
                  isReserved -> Color(0xFF9E7A80)
                  isSlotSelected -> Color(0xFF1E1302)
                  else -> TextWhite
                }
              )
              if (isReserved) {
                Text(
                  text = "RESERVED",
                  fontFamily = MontserratFamily,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFFF5252)
                )
              }
            }
          }
        }
      }

      // ─────────────────────────────────────────────────────────────
      // 4. PREFERRED STAFF SELECTION (OPTIONAL)
      // ─────────────────────────────────────────────────────────────
      if (staffList.isNotEmpty()) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
          text = "PREFERRED STYLIST / SPECIALIST",
          fontFamily = MontserratFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = GoldPrimary,
          letterSpacing = 1.sp,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Any Available option
          val isAnySelected = selectedStaff == null
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isAnySelected) GoldTagBg else LuxurySurface)
              .border(1.dp, if (isAnySelected) GoldPrimary else GoldBorder.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
              .clickable { selectedStaff = null }
              .padding(horizontal = 12.dp, vertical = 8.dp)
              .testTag("staff_any_available")
          ) {
            Text(
              text = "Any Available Professional",
              fontFamily = MontserratFamily,
              fontWeight = if (isAnySelected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 11.5.sp,
              color = if (isAnySelected) GoldPrimary else TextIvory
            )
          }

          staffList.forEach { staff ->
            val isStaffSelected = selectedStaff?.staffId == staff.staffId
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isStaffSelected) GoldTagBg else LuxurySurface)
                .border(1.dp, if (isStaffSelected) GoldPrimary else GoldBorder.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .clickable { selectedStaff = staff }
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("staff_item_${staff.staffId}")
            ) {
              Column {
                Text(
                  text = staff.name,
                  fontFamily = MontserratFamily,
                  fontWeight = if (isStaffSelected) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 11.5.sp,
                  color = if (isStaffSelected) GoldPrimary else TextWhite
                )
                Text(
                  text = staff.role,
                  fontFamily = MontserratFamily,
                  fontSize = 9.5.sp,
                  color = TextSecondary
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // ─────────────────────────────────────────────────────────────
      // 5. BOOKING SUMMARY & CONFIRMATION
      // ─────────────────────────────────────────────────────────────
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("view_salon_booking_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CustomerCardBg),
        border = BorderStroke(
          1.5.dp,
          if (salon.isActive) GoldBorder else Color(0xFFD32F2F)
        )
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "BOOKING SUMMARY",
                fontFamily = MontserratFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextWhite,
                letterSpacing = 0.5.sp
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (salon.isActive) Color(0xFF0C2414) else Color(0xFF380E13))
                .border(
                  0.5.dp,
                  if (salon.isActive) Color(0xFF4CAF50) else Color(0xFFFF5252),
                  RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = if (salon.isActive) "ACTIVE PARTNER" else "DEACTIVATED",
                fontFamily = MontserratFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = if (salon.isActive) Color(0xFF4CAF50) else Color(0xFFFF5252)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Client Details
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(LuxuryBlackElevated)
              .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text("Client Name", fontSize = 10.5.sp, color = TextSecondary)
              Text(customer.fullName, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp, color = TextWhite)
            }
            Column(horizontalAlignment = Alignment.End) {
              Text("Client Mobile", fontSize = 10.5.sp, color = TextSecondary)
              Text(customer.normalizedPhone, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp, color = GoldPrimary)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Selected Services Breakdown
          Text(
            text = "Services Selected (${selectedServices.size}):",
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(6.dp))

          if (selectedServices.isEmpty()) {
            Text(
              text = "No services selected yet. Please select at least one service above.",
              fontFamily = MontserratFamily,
              fontSize = 11.5.sp,
              color = TextMuted
            )
          } else {
            Column(
              modifier = Modifier.fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              selectedServices.forEach { srv ->
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "• ${srv.name}",
                    fontFamily = MontserratFamily,
                    fontSize = 12.sp,
                    color = TextIvory,
                    modifier = Modifier.weight(1f)
                  )
                  Text(
                    text = "$${String.format("%.2f", srv.price)}",
                    fontFamily = MontserratFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = GoldPrimary
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          HorizontalDivider(color = GoldBorder.copy(alpha = 0.3f), thickness = 1.dp)
          Spacer(modifier = Modifier.height(12.dp))

          // Schedule Details: Date, Time Slot, and Professional
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text("Appointment Date", fontSize = 11.sp, color = TextSecondary)
              Text(selectedDate, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextWhite)
            }
            Column(horizontalAlignment = Alignment.End) {
              Text("Time Slot", fontSize = 11.sp, color = TextSecondary)
              Text(
                text = selectedTimeSlot ?: "Select a slot",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = if (selectedTimeSlot != null) GoldAccent else Color(0xFFFFB4C0),
                modifier = Modifier.testTag("summary_selected_slot")
              )
            }
          }

          if (selectedStaff != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Assigned Professional", fontSize = 11.sp, color = TextSecondary)
              Text(selectedStaff!!.name, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = GoldPrimary)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          HorizontalDivider(color = GoldBorder.copy(alpha = 0.3f), thickness = 1.dp)
          Spacer(modifier = Modifier.height(12.dp))

          // Grand Total Amount Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "TOTAL AMOUNT",
                fontFamily = MontserratFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = TextSecondary
              )
              Text(
                text = "Estimated duration: $totalDuration mins",
                fontFamily = MontserratFamily,
                fontSize = 10.5.sp,
                color = TextMuted
              )
            }

            Text(
              text = "$${String.format("%.2f", totalPrice)}",
              fontFamily = MontserratFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp,
              color = GoldPrimary,
              modifier = Modifier.testTag("summary_total_price")
            )
          }

          // Error Message Alert
          if (bookingErrorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF330B14))
                .border(1.dp, Color(0xFFD32F2F), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("booking_error_banner")
            ) {
              Text(
                text = bookingErrorMessage.orEmpty(),
                fontFamily = MontserratFamily,
                fontSize = 12.sp,
                color = Color(0xFFFFB4C0)
              )
            }
          }

          // Success Message Alert
          if (confirmedBooking != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0C2414))
                .border(1.dp, Color(0xFF2E7D32), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .testTag("booking_success_banner")
            ) {
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Booking Confirmed & Owner Notified!",
                    fontFamily = MontserratFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                    color = Color(0xFF4CAF50)
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Reference ID: ${confirmedBooking!!.bookingId}\nScheduled: ${confirmedBooking!!.bookingDate} at ${confirmedBooking!!.timeSlot}\nTotal Paid: $${String.format("%.2f", confirmedBooking!!.totalAmount)}",
                  fontFamily = MontserratFamily,
                  fontSize = 11.5.sp,
                  color = Color(0xFFA5D6A7)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // CONFIRM BOOKING BUTTON
          val canConfirm = salon.isActive && selectedServices.isNotEmpty() && selectedTimeSlot != null && !isConfirming
          Button(
            onClick = {
              if (!salon.isActive) {
                bookingErrorMessage = "This salon is currently deactivated and not accepting new bookings."
                return@Button
              }
              if (selectedServices.isEmpty()) {
                bookingErrorMessage = "Please select at least one service."
                return@Button
              }
              val slot = selectedTimeSlot
              if (slot == null) {
                bookingErrorMessage = "Please select an available time slot."
                return@Button
              }

              isConfirming = true
              bookingErrorMessage = null

              coroutineScope.launch {
                val result = salonRepository.createBooking(
                  salonId = salon.salonId,
                  customerId = customer.customerId,
                  customerName = customer.fullName,
                  customerPhone = customer.normalizedPhone,
                  selectedServices = selectedServices.toList(),
                  bookingDate = selectedDate,
                  timeSlot = slot,
                  staffId = selectedStaff?.staffId,
                  staffName = selectedStaff?.name
                )
                isConfirming = false
                result.fold(
                  onSuccess = { booking ->
                    confirmedBooking = booking
                    bookingErrorMessage = null
                  },
                  onFailure = { error ->
                    bookingErrorMessage = error.message ?: "Booking failed. Slot may have been reserved."
                  }
                )
              }
            },
            enabled = salon.isActive && !isConfirming,
            colors = ButtonDefaults.buttonColors(
              containerColor = Color.Transparent,
              disabledContainerColor = Color(0xFF2B0A0D)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .then(
                if (salon.isActive) {
                  Modifier.background(
                    Brush.horizontalGradient(listOf(GoldGradientStart, GoldPrimary, GoldAccent)),
                    RoundedCornerShape(12.dp)
                  )
                } else {
                  Modifier.border(1.dp, Color(0xFFD32F2F), RoundedCornerShape(12.dp))
                }
              )
              .testTag("btn_confirm_booking")
          ) {
            if (isConfirming) {
              CircularProgressIndicator(
                color = Color(0xFF1E1302),
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
              )
            } else {
              Icon(
                imageVector = if (salon.isActive) Icons.Default.CheckCircle else Icons.Default.Block,
                contentDescription = null,
                tint = if (salon.isActive) Color(0xFF1E1302) else Color(0xFFFF8A98),
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (salon.isActive) "CONFIRM BOOKING" else "BOOKING UNAVAILABLE (SALON DEACTIVATED)",
                fontFamily = MontserratFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (salon.isActive) Color(0xFF1E1302) else Color(0xFFFF8A98)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}

/**
 * Opens Google Maps to the salon's GPS coordinates or address.
 */
fun openGoogleMapsDirections(context: Context, salon: SalonEntity) {
  val uri = if (salon.latitude != null && salon.longitude != null) {
    Uri.parse("geo:${salon.latitude},${salon.longitude}?q=${salon.latitude},${salon.longitude}(${Uri.encode(salon.salonName)})")
  } else if (!salon.address.isNullOrBlank()) {
    Uri.parse("geo:0,0?q=${Uri.encode(salon.address)}")
  } else {
    Uri.parse("geo:0,0?q=${Uri.encode(salon.salonName)}")
  }

  val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
    setPackage("com.google.android.apps.maps")
  }
  try {
    context.startActivity(mapIntent)
  } catch (e: Exception) {
    val webUri = if (salon.latitude != null && salon.longitude != null) {
      Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${salon.latitude},${salon.longitude}")
    } else {
      Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(salon.address ?: salon.salonName)}")
    }
    context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
  }
}
