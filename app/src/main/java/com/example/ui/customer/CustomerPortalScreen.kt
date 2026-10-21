package com.example.ui.customer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CustomerEntity
import com.example.ui.components.SalonMateLogoBadge
import com.example.ui.theme.CustomerCardBg
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBorder
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

data class PortalServiceItem(
  val iconRes: Int,
  val titleRes: Int
)

@Composable
fun CustomerPortalScreen(
  customer: CustomerEntity,
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onLogout() }

  val services = listOf(
    PortalServiceItem(R.drawable.ic_scissors, R.string.service_haircut),
    PortalServiceItem(R.drawable.ic_facial_care, R.string.service_facial),
    PortalServiceItem(R.drawable.ic_makeup_beauty, R.string.service_makeup),
    PortalServiceItem(R.drawable.ic_nail_care, R.string.service_nail),
    PortalServiceItem(R.drawable.ic_spa_lotus, R.string.service_spa),
    PortalServiceItem(R.drawable.ic_beard_grooming, R.string.service_beard)
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(LuxuryBlack)
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("customer_portal_screen_root"),
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
      // Top Navigation Bar with Logo and Sign Out
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          SalonMateLogoBadge(
            modifier = Modifier
              .size(44.dp)
              .testTag("portal_top_logo")
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
              text = stringResource(R.string.portal_title),
              fontFamily = MontserratFamily,
              fontSize = 11.sp,
              color = PinkPrimary
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
            .testTag("customer_logout_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = "Sign Out",
            tint = GoldPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Customer Profile Summary Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("customer_profile_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CustomerCardBg),
        border = BorderStroke(1.dp, PinkBorder)
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
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(LuxuryBlackElevated)
                  .border(1.dp, GoldBorder, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = null,
                  tint = GoldPrimary,
                  modifier = Modifier.size(26.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = stringResource(R.string.portal_welcome_back),
                  fontFamily = MontserratFamily,
                  fontSize = 12.sp,
                  color = TextMuted
                )
                Text(
                  text = customer.fullName,
                  fontFamily = MontserratFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 18.sp,
                  color = TextWhite,
                  modifier = Modifier.testTag("customer_profile_name")
                )
              }
            }

            // Active Customer Status Badge
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
                  text = "VERIFIED",
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

          // Detail rows: Customer ID & Mobile Number
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(LuxuryBlackElevated)
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = stringResource(R.string.portal_customer_id),
                fontFamily = MontserratFamily,
                fontSize = 11.sp,
                color = TextSecondary
              )
              Text(
                text = customer.customerId,
                fontFamily = MontserratFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = GoldPrimary,
                modifier = Modifier.testTag("customer_profile_id")
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = stringResource(R.string.portal_phone),
                fontFamily = MontserratFamily,
                fontSize = 11.sp,
                color = TextSecondary
              )
              Text(
                text = customer.normalizedPhone,
                fontFamily = MontserratFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = TextWhite,
                modifier = Modifier.testTag("customer_profile_phone")
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Section Title: Available Luxury Services
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "EXPLORE SERVICES",
          fontFamily = MontserratFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = GoldPrimary,
          letterSpacing = 1.sp
        )
        Text(
          text = "Men & Women",
          fontFamily = MontserratFamily,
          fontSize = 11.sp,
          color = TextSecondary
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Services Grid
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        val pairs = services.chunked(2)
        pairs.forEach { rowServices ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            rowServices.forEach { service ->
              Card(
                modifier = Modifier
                  .weight(1f)
                  .height(96.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LuxurySurface),
                border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.4f))
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                  verticalArrangement = Arrangement.Center,
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Icon(
                    painter = painterResource(id = service.iconRes),
                    contentDescription = null,
                    tint = PinkPrimary,
                    modifier = Modifier.size(24.dp)
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = stringResource(id = service.titleRes).replace("\n", " "),
                    fontFamily = MontserratFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = TextIvory,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                  )
                }
              }
            }
            if (rowServices.size == 1) {
              Spacer(modifier = Modifier.weight(1f))
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Logout / Sign Out Button
      Button(
        onClick = onLogout,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, GoldBorder),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .background(LuxurySurface, RoundedCornerShape(12.dp))
          .testTag("customer_sign_out_button")
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
