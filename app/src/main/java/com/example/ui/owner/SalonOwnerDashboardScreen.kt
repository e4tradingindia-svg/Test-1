package com.example.ui.owner

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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
import com.example.ui.theme.PinkBorder
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.TextIvory
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextWhite

data class OwnerManagementTool(
  val titleRes: Int,
  val subtitle: String,
  val icon: ImageVector
)

@Composable
fun SalonOwnerDashboardScreen(
  profile: OwnerBusinessProfile,
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
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

        // Sign Out Icon Button
        IconButton(
          onClick = onLogout,
          modifier = Modifier
            .size(42.dp)
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
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
              Column {
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

            // Partner Status Badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(GoldTagBg)
                .border(0.5.dp, GoldBorder, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = GoldPrimary,
                  modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = stringResource(R.string.owner_status_verified),
                  fontFamily = MontserratFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp,
                  color = GoldPrimary,
                  letterSpacing = 0.5.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

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
