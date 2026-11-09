package com.example.ui.owner

import android.app.Activity
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.data.repository.SalonRepository
import com.example.ui.components.SalonMateLogoBadge
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldTagBg
import com.example.ui.theme.LuxuryBlack
import com.example.ui.theme.LuxuryBlackElevated
import com.example.ui.theme.LuxurySurface
import com.example.ui.theme.MontserratFamily
import com.example.ui.theme.OwnerCardBg
import com.example.ui.theme.TextIvory
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.launch
import java.io.File

data class OwnerManagementTool(
  val titleRes: Int,
  val subtitle: String,
  val icon: ImageVector
)

@Composable
fun SalonOwnerDashboardScreen(
  profile: OwnerBusinessProfile,
  salonRepository: SalonRepository? = null,
  onLogout: () -> Unit,
  onEditProfileClick: () -> Unit = {},
  onToggleActiveStatus: (Boolean) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  var localIsActive by remember(profile.salonId) { mutableStateOf(profile.isActive) }

  // Observe persisted database state if repository is available
  val dbSalon by salonRepository?.observeSalonById(profile.salonId)?.collectAsState(initial = null)
    ?: remember { mutableStateOf(null) }
  val currentIsActive = dbSalon?.isActive ?: localIsActive

  BackHandler {
    (context as? Activity)?.finish()
  }

  val tools = listOf(
    OwnerManagementTool(R.string.owner_tool_services, "Haircut, Spa, Facial & Pricing", Icons.Default.ContentCut),
    OwnerManagementTool(R.string.owner_tool_staff, "Barbers, Stylists & Rosters", Icons.Default.Group),
    OwnerManagementTool(R.string.owner_tool_hours, "Mon - Sun opening schedules", Icons.Default.AccessTime),
    OwnerManagementTool(R.string.owner_tool_photos, "Interior, Work & Showcase", Icons.Default.PhotoCamera),
    OwnerManagementTool(R.string.owner_tool_bookings, "Upcoming client appointments", Icons.Default.CalendarMonth),
    OwnerManagementTool(R.string.owner_tool_notifications, "Client reminders & alerts", Icons.Default.Notifications)
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(LuxuryBlack)
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("owner_dashboard_screen_root"),
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
        Row(verticalAlignment = Alignment.CenterVertically) {
          SalonMateLogoBadge(
            modifier = Modifier
              .size(44.dp)
              .testTag("owner_portal_top_logo")
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "SalonMate",
              fontFamily = MontserratFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = GoldPrimary
            )
            Text(
              text = stringResource(R.string.owner_dashboard_title),
              fontFamily = MontserratFamily,
              fontSize = 11.sp,
              color = TextSecondary
            )
          }
        }

        // Top right actions: ✏️ Edit Profile & Sign Out
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onEditProfileClick,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(LuxurySurface)
              .border(1.dp, GoldBorder.copy(alpha = 0.6f), CircleShape)
              .testTag("owner_edit_profile_top_button")
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit Business Profile",
              tint = GoldPrimary,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          // Sign Out Icon Button
          IconButton(
            onClick = onLogout,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(LuxurySurface)
              .border(1.dp, GoldBorder.copy(alpha = 0.6f), CircleShape)
              .testTag("owner_logout_icon_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ExitToApp,
              contentDescription = "Sign Out Business",
              tint = GoldPrimary,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Salon Business Profile Summary Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("owner_profile_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = OwnerCardBg),
        border = BorderStroke(1.5.dp, GoldBorder)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          // Banner thumbnail if uploaded
          val bannerUri = profile.bannerUri
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
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, GoldBorder.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .clickable { onEditProfileClick() }
            ) {
              Image(
                bitmap = bBitmap.asImageBitmap(),
                contentDescription = "Salon Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
              )
            }
            Spacer(modifier = Modifier.height(14.dp))
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(50.dp)
                  .clip(CircleShape)
                  .background(LuxuryBlackElevated)
                  .border(1.dp, GoldBorder, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Storefront,
                  contentDescription = null,
                  tint = GoldPrimary,
                  modifier = Modifier.size(28.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = profile.salonName,
                  fontFamily = MontserratFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 18.sp,
                  color = TextWhite,
                  modifier = Modifier.testTag("owner_profile_salon_name")
                )
                Text(
                  text = "${profile.ownerName} • ${profile.businessType}",
                  fontFamily = MontserratFamily,
                  fontSize = 12.sp,
                  color = TextSecondary
                )
              }
            }

            // ✏️ Edit Button Badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(GoldTagBg)
                .border(0.5.dp, GoldBorder, RoundedCornerShape(8.dp))
                .clickable { onEditProfileClick() }
                .padding(horizontal = 8.dp, vertical = 5.dp)
                .testTag("owner_edit_profile_badge"),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Edit,
                  contentDescription = "Edit Profile",
                  tint = GoldPrimary,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "EDIT",
                  fontFamily = MontserratFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                  color = GoldPrimary,
                  letterSpacing = 0.5.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Active Partner Clickable Horizontal Toggle Card
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .clickable {
                val newStatus = !currentIsActive
                localIsActive = newStatus
                coroutineScope.launch {
                  salonRepository?.updateSalonActiveStatus(profile.salonId, newStatus)
                  onToggleActiveStatus(newStatus)
                }
              }
              .testTag("active_partner_toggle_button"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (currentIsActive) Color(0xFF0C2414) else Color(0xFF2B0A0D)
            ),
            border = BorderStroke(
              width = 1.dp,
              color = if (currentIsActive) Color(0xFF2E7D32) else Color(0xFFD32F2F)
            )
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              // Left: Status Icon and Horizontal Text Info
              Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (currentIsActive) Color(0xFF1B4D27) else Color(0xFF4A1217)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (currentIsActive) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (currentIsActive) Color(0xFF4CAF50) else Color(0xFFFF5252),
                    modifier = Modifier.size(16.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "Active Partner:",
                      fontFamily = MontserratFamily,
                      fontWeight = FontWeight.SemiBold,
                      fontSize = 12.sp,
                      color = TextIvory
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = if (currentIsActive) "ACTIVE" else "DEACTIVATED",
                      fontFamily = MontserratFamily,
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp,
                      color = if (currentIsActive) Color(0xFF4CAF50) else Color(0xFFFF5252),
                      modifier = Modifier.testTag("active_partner_status_text")
                    )
                  }
                  Text(
                    text = if (currentIsActive)
                      "Online • Can receive customer bookings/orders"
                    else
                      "Unavailable • Cannot receive new online bookings/orders",
                    fontFamily = MontserratFamily,
                    fontSize = 10.5.sp,
                    color = if (currentIsActive) Color(0xFFA5D6A7) else Color(0xFFFFB4C0),
                    maxLines = 1
                  )
                }
              }

              Spacer(modifier = Modifier.width(8.dp))

              // Right: Horizontal Switch Button / Badge
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(20.dp))
                  .background(if (currentIsActive) Color(0xFF143B1E) else Color(0xFF3E0E13))
                  .border(
                    width = 1.dp,
                    color = if (currentIsActive) Color(0xFF4CAF50) else Color(0xFFFF5252),
                    shape = RoundedCornerShape(20.dp)
                  )
                  .padding(horizontal = 12.dp, vertical = 6.dp)
                  .testTag("active_partner_switch_badge"),
                contentAlignment = Alignment.Center
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center
                ) {
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .clip(CircleShape)
                      .background(if (currentIsActive) Color(0xFF4CAF50) else Color(0xFFFF5252))
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = if (currentIsActive) "ACTIVE" else "DEACTIVATED",
                    fontFamily = MontserratFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp,
                    color = if (currentIsActive) Color(0xFF4CAF50) else Color(0xFFFF5252),
                    letterSpacing = 0.5.sp
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Address & Mobile Contact Details Row
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(LuxuryBlackElevated)
              .clickable { onEditProfileClick() }
              .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Address
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (!profile.address.isNullOrBlank()) profile.address else "Tap to add salon address",
                fontFamily = MontserratFamily,
                fontSize = 12.sp,
                color = if (!profile.address.isNullOrBlank()) TextIvory else TextSecondary,
                modifier = Modifier.testTag("owner_profile_address_text")
              )
            }

            // Mobile
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = profile.mobileNumber,
                fontFamily = MontserratFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextWhite,
                modifier = Modifier.testTag("owner_profile_mobile_text")
              )
            }

            // GPS coordinates if available
            if (profile.latitude != null && profile.longitude != null) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = GoldAccent,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "GPS: ${profile.latitude}, ${profile.longitude}",
                  fontFamily = MontserratFamily,
                  fontSize = 11.sp,
                  color = GoldAccent,
                  modifier = Modifier.testTag("owner_profile_gps_text")
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Detail stats row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(LuxuryBlackElevated)
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = stringResource(R.string.owner_stat_bookings),
                fontFamily = MontserratFamily,
                fontSize = 11.sp,
                color = TextSecondary
              )
              Text(
                text = "14",
                fontFamily = MontserratFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = GoldPrimary
              )
            }

            Box(
              modifier = Modifier
                .width(1.dp)
                .height(30.dp)
                .background(LuxurySurface)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = stringResource(R.string.owner_stat_revenue),
                fontFamily = MontserratFamily,
                fontSize = 11.sp,
                color = TextSecondary
              )
              Text(
                text = "$480",
                fontFamily = MontserratFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextWhite
              )
            }

            Box(
              modifier = Modifier
                .width(1.dp)
                .height(30.dp)
                .background(LuxurySurface)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = stringResource(R.string.owner_stat_rating),
                fontFamily = MontserratFamily,
                fontSize = 11.sp,
                color = TextSecondary
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Star,
                  contentDescription = null,
                  tint = GoldPrimary,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "4.9",
                  fontFamily = MontserratFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  color = GoldPrimary
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Section Title: Management Tools
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = stringResource(R.string.owner_tools_title),
          fontFamily = MontserratFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = GoldPrimary,
          letterSpacing = 1.sp
        )
        Text(
          text = "Active Suite",
          fontFamily = MontserratFamily,
          fontSize = 11.sp,
          color = TextSecondary
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Management Tools List
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        tools.forEach { tool ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .height(68.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = LuxurySurface),
            border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.4f))
          ) {
            Row(
              modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(LuxuryBlackElevated)
                  .border(0.5.dp, GoldBorder.copy(alpha = 0.6f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = tool.icon,
                  contentDescription = null,
                  tint = GoldPrimary,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(14.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = stringResource(id = tool.titleRes),
                  fontFamily = MontserratFamily,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 13.5.sp,
                  color = TextWhite
                )
                Text(
                  text = tool.subtitle,
                  fontFamily = MontserratFamily,
                  fontSize = 11.sp,
                  color = TextSecondary,
                  maxLines = 1
                )
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(GoldTagBg)
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text(
                  text = "READY",
                  fontFamily = MontserratFamily,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = GoldPrimary
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Sign Out Button
      Button(
        onClick = onLogout,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, GoldBorder),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .background(LuxurySurface, RoundedCornerShape(12.dp))
          .testTag("owner_sign_out_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ExitToApp,
          contentDescription = null,
          tint = GoldPrimary,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = stringResource(R.string.btn_logout),
          fontFamily = MontserratFamily,
          fontWeight = FontWeight.SemiBold,
          fontSize = 14.sp,
          color = GoldPrimary
        )
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
