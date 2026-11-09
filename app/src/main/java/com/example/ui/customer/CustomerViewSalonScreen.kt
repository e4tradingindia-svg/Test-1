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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.data.model.SalonEntity
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
import java.io.File

@Composable
fun CustomerViewSalonScreen(
  salon: SalonEntity,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  var bookingMessage by remember { mutableStateOf<String?>(null) }

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
          .height(200.dp)
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
          // Salon Name & Badge
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

          Spacer(modifier = Modifier.height(14.dp))

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

      Spacer(modifier = Modifier.height(18.dp))

      // Book Appointment / Order Service Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("view_salon_booking_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (salon.isActive) CustomerCardBg else Color(0xFF22080B)
        ),
        border = BorderStroke(
          1.dp,
          if (salon.isActive) PinkBorder else Color(0xFFD32F2F)
        )
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (salon.isActive) Icons.Default.CalendarMonth else Icons.Default.Block,
                contentDescription = null,
                tint = if (salon.isActive) GoldPrimary else Color(0xFFFF5252),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (salon.isActive) "Book Appointments & Orders" else "Bookings Unavailable",
                fontFamily = MontserratFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextWhite
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
                text = if (salon.isActive) "ACTIVE" else "DEACTIVATED",
                fontFamily = MontserratFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = if (salon.isActive) Color(0xFF4CAF50) else Color(0xFFFF5252)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = if (salon.isActive)
              "This salon is active and accepting instant client appointments and beauty service orders."
            else
              "This salon is currently deactivated by the owner and cannot receive new online bookings or orders.",
            fontFamily = MontserratFamily,
            fontSize = 12.sp,
            color = if (salon.isActive) TextSecondary else Color(0xFFFFB4C0)
          )

          if (bookingMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (salon.isActive) Color(0xFF0C2414) else Color(0xFF380E13))
                .border(
                  1.dp,
                  if (salon.isActive) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                  RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("booking_status_feedback")
            ) {
              Text(
                text = bookingMessage.orEmpty(),
                fontFamily = MontserratFamily,
                fontSize = 11.5.sp,
                color = if (salon.isActive) Color(0xFFA5D6A7) else Color(0xFFFFB4C0)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = {
              if (salon.isActive) {
                bookingMessage = "Booking request submitted! The active salon has received your order."
              } else {
                bookingMessage = "Booking rejected: Salon is deactivated and not accepting appointments."
              }
            },
            enabled = salon.isActive,
            colors = ButtonDefaults.buttonColors(
              containerColor = Color.Transparent,
              disabledContainerColor = Color(0xFF2B0A0D)
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .then(
                if (salon.isActive) {
                  Modifier.background(
                    Brush.horizontalGradient(listOf(GoldGradientStart, GoldPrimary, GoldAccent)),
                    RoundedCornerShape(10.dp)
                  )
                } else {
                  Modifier.border(1.dp, Color(0xFFD32F2F), RoundedCornerShape(10.dp))
                }
              )
              .testTag("btn_book_salon")
          ) {
            Icon(
              imageVector = if (salon.isActive) Icons.Default.CalendarMonth else Icons.Default.Block,
              contentDescription = null,
              tint = if (salon.isActive) Color(0xFF1E1302) else Color(0xFFFF8A98),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (salon.isActive) "BOOK APPOINTMENT / ORDER" else "BOOKING UNAVAILABLE (DEACTIVATED)",
              fontFamily = MontserratFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = if (salon.isActive) Color(0xFF1E1302) else Color(0xFFFF8A98)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Action Buttons: Get Directions (Google Maps) & Call Salon
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Get Directions button
        Button(
          onClick = {
            openGoogleMapsDirections(context, salon)
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
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
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = stringResource(R.string.customer_btn_get_directions),
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0xFF1E1302)
          )
        }

        // Call Salon Button
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
            .height(50.dp)
            .testTag("btn_call_salon")
        ) {
          Icon(
            imageVector = Icons.Default.Call,
            contentDescription = null,
            tint = GoldPrimary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = stringResource(R.string.customer_btn_call_salon),
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
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
    // If Google Maps app is not installed, open via browser
    val webUri = if (salon.latitude != null && salon.longitude != null) {
      Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${salon.latitude},${salon.longitude}")
    } else {
      Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(salon.address ?: salon.salonName)}")
    }
    context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
  }
}
