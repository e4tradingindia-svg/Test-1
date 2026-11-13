package com.example.ui.customer

import android.app.Activity
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CustomerEntity
import com.example.data.model.SalonEntity
import com.example.ui.components.SalonMateLogoBadge
import com.example.ui.theme.CustomerCardBg
import com.example.ui.theme.DarkBorder
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
import java.io.File

data class PortalServiceItem(
  val iconRes: Int,
  val titleRes: Int,
  val keyTag: String
)

/**
 * Filter options for salons by their registered business category.
 */
enum class SalonCategoryFilter(val displayName: String, val testId: String) {
  ALL("All (Men & Women)", "all"),
  MENS("Men's Salon", "mens"),
  WOMENS("Women's Beauty Parlour", "womens"),
  UNISEX("Unisex Salon", "unisex");

  fun matches(businessType: String): Boolean {
    return when (this) {
      ALL -> true
      MENS -> !businessType.contains("Women", ignoreCase = true) &&
              !businessType.contains("Unisex", ignoreCase = true) &&
              (businessType.contains("Men", ignoreCase = true) || businessType.contains("Barber", ignoreCase = true))
      WOMENS -> !businessType.contains("Unisex", ignoreCase = true) &&
                (businessType.contains("Women", ignoreCase = true) || businessType.contains("Parlour", ignoreCase = true))
      UNISEX -> businessType.contains("Unisex", ignoreCase = true)
    }
  }
}

/**
 * Helper to compute and format distance from coordinates.
 */
fun formatSalonDistance(salon: SalonEntity): String {
  val lat = salon.latitude
  val lng = salon.longitude
  if (lat == null || lng == null) return "1.2 km away"
  val refLat = 37.7700
  val refLng = -122.4150
  val dLat = Math.toRadians(lat - refLat)
  val dLng = Math.toRadians(lng - refLng)
  val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(Math.toRadians(refLat)) * Math.cos(Math.toRadians(lat)) *
      Math.sin(dLng / 2) * Math.sin(dLng / 2)
  val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
  val distKm = 6371.0 * c
  return String.format(java.util.Locale.US, "%.1f km away", distKm.coerceAtLeast(0.4))
}

@Composable
fun CustomerPortalScreen(
  customer: CustomerEntity,
  salons: List<SalonEntity> = emptyList(),
  onSalonSelected: (SalonEntity) -> Unit = {},
  onLogout: () -> Unit,
  onEditProfileClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  BackHandler {
    (context as? Activity)?.finish()
  }

  // 6 Services - displayed as compact, non-clickable informational labels
  val services = listOf(
    PortalServiceItem(R.drawable.ic_scissors, R.string.service_haircut, "haircut"),
    PortalServiceItem(R.drawable.ic_facial_care, R.string.service_facial, "facial"),
    PortalServiceItem(R.drawable.ic_makeup_beauty, R.string.service_makeup, "makeup"),
    PortalServiceItem(R.drawable.ic_nail_care, R.string.service_nail, "nail"),
    PortalServiceItem(R.drawable.ic_spa_lotus, R.string.service_spa, "spa"),
    PortalServiceItem(R.drawable.ic_beard_grooming, R.string.service_beard, "beard")
  )

  // Filtering & Search state
  var selectedCategory by remember { mutableStateOf(SalonCategoryFilter.ALL) }
  var searchQuery by remember { mutableStateOf("") }

  val filteredSalons = remember(salons, selectedCategory, searchQuery) {
    salons.filter { salon ->
      val matchesCategory = selectedCategory.matches(salon.businessType)
      val matchesSearch = if (searchQuery.isBlank()) true else {
        salon.salonName.contains(searchQuery, ignoreCase = true) ||
        (salon.address?.contains(searchQuery, ignoreCase = true) == true) ||
        salon.businessType.contains(searchQuery, ignoreCase = true)
      }
      matchesCategory && matchesSearch
    }
  }

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

        // Top right actions: Edit Profile & Sign Out
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onEditProfileClick,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(LuxurySurface)
              .border(1.dp, GoldBorder.copy(alpha = 0.6f), CircleShape)
              .testTag("btn_top_edit_profile")
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = stringResource(R.string.btn_edit_profile),
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
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Customer Profile Summary Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("customer_profile_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CustomerCardBg),
        border = BorderStroke(1.dp, PinkBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              // Profile Photo or Default Avatar Icon
              Box(
                modifier = Modifier
                  .size(50.dp)
                  .clip(CircleShape)
                  .background(LuxuryBlackElevated)
                  .border(1.5.dp, GoldBorder, CircleShape)
                  .clickable { onEditProfileClick() }
                  .testTag("portal_profile_avatar"),
                contentAlignment = Alignment.Center
              ) {
                val photoUri = customer.profilePhotoUri
                var loadedBitmap by remember(photoUri) {
                  mutableStateOf<android.graphics.Bitmap?>(null)
                }

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
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(26.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column {
                Text(
                  text = stringResource(R.string.portal_welcome_back),
                  fontFamily = MontserratFamily,
                  fontSize = 11.5.sp,
                  color = TextMuted
                )
                Text(
                  text = customer.fullName,
                  fontFamily = MontserratFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 17.sp,
                  color = TextWhite,
                  modifier = Modifier.testTag("customer_profile_name")
                )
              }
            }

            // Edit Profile Button pill
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(GoldTagBg)
                .border(0.5.dp, GoldBorder, RoundedCornerShape(8.dp))
                .clickable { onEditProfileClick() }
                .padding(horizontal = 9.dp, vertical = 5.dp)
                .testTag("btn_edit_profile"),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Edit,
                  contentDescription = null,
                  tint = GoldPrimary,
                  modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "EDIT PROFILE",
                  fontFamily = MontserratFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.5.sp,
                  color = GoldPrimary,
                  letterSpacing = 0.5.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Detail rows: Customer ID & Mobile Number
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(LuxuryBlackElevated)
              .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = stringResource(R.string.portal_customer_id),
                fontFamily = MontserratFamily,
                fontSize = 10.5.sp,
                color = TextSecondary
              )
              Text(
                text = customer.customerId,
                fontFamily = MontserratFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.5.sp,
                color = GoldPrimary,
                modifier = Modifier.testTag("customer_profile_id")
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = stringResource(R.string.portal_phone),
                fontFamily = MontserratFamily,
                fontSize = 10.5.sp,
                color = TextSecondary
              )
              Text(
                text = customer.normalizedPhone,
                fontFamily = MontserratFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.5.sp,
                color = TextWhite,
                modifier = Modifier.testTag("customer_profile_phone")
              )
            }
          }

          // Optional Address Display Row
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(LuxuryBlackElevated)
              .clickable { onEditProfileClick() }
              .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = GoldPrimary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = stringResource(R.string.portal_address_title),
                fontFamily = MontserratFamily,
                fontSize = 10.5.sp,
                color = TextSecondary
              )
              Text(
                text = if (!customer.address.isNullOrBlank()) customer.address else stringResource(R.string.portal_no_address),
                fontFamily = MontserratFamily,
                fontWeight = if (!customer.address.isNullOrBlank()) FontWeight.Medium else FontWeight.Normal,
                fontSize = 12.sp,
                color = if (!customer.address.isNullOrBlank()) TextIvory else TextMuted,
                modifier = Modifier.testTag("customer_profile_address")
              )
            }
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = null,
              tint = TextSecondary,
              modifier = Modifier.size(13.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // ─────────────────────────────────────────────────────────────
      // 1. EXPLORE SERVICES (Compact, non-clickable informational labels)
      // ─────────────────────────────────────────────────────────────
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "EXPLORE SERVICES",
          fontFamily = MontserratFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 11.5.sp,
          color = GoldPrimary,
          letterSpacing = 0.8.sp
        )
        Text(
          text = "Men & Women Services",
          fontFamily = MontserratFamily,
          fontSize = 10.5.sp,
          color = TextMuted
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Compact 6 Service Badges: 2 rows x 3 columns, non-clickable, non-button appearance
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("explore_services_compact_container"),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        val rows = services.chunked(3)
        rows.forEach { rowServices ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            rowServices.forEach { service ->
              Row(
                modifier = Modifier
                  .weight(1f)
                  .height(34.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(LuxurySurface.copy(alpha = 0.6f))
                  .border(0.5.dp, DarkBorder, RoundedCornerShape(8.dp))
                  .padding(horizontal = 6.dp)
                  .testTag("service_label_${service.keyTag}"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  painter = painterResource(id = service.iconRes),
                  contentDescription = null,
                  tint = PinkPrimary,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = stringResource(id = service.titleRes).replace("\n", " "),
                  fontFamily = MontserratFamily,
                  fontWeight = FontWeight.Medium,
                  fontSize = 10.sp,
                  color = TextSecondary,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      // ─────────────────────────────────────────────────────────────
      // 2. NEARBY SALONS & PARLOURS (High Priority Section)
      // ─────────────────────────────────────────────────────────────
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "NEARBY SALONS & PARLOURS",
            fontFamily = MontserratFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = GoldPrimary,
            letterSpacing = 1.sp,
            modifier = Modifier.testTag("section_title_nearby_salons")
          )
          Text(
            text = "Verified salons and beauty parlours around you",
            fontFamily = MontserratFamily,
            fontSize = 10.5.sp,
            color = TextMuted
          )
        }
        Text(
          text = "${filteredSalons.size} Found",
          fontFamily = MontserratFamily,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = PinkPrimary,
          modifier = Modifier.testTag("text_filtered_salon_count")
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Search Bar (Quick filter by name or address)
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = {
          Text(
            text = "Search salon by name, service or address...",
            fontFamily = MontserratFamily,
            fontSize = 11.5.sp,
            color = TextMuted
          )
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = GoldPrimary,
            modifier = Modifier.size(18.dp)
          )
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(
              onClick = { searchQuery = "" },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Clear search",
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = LuxurySurface,
          unfocusedContainerColor = LuxurySurface,
          focusedBorderColor = GoldBorder,
          unfocusedBorderColor = DarkBorder,
          focusedTextColor = TextWhite,
          unfocusedTextColor = TextWhite
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("portal_search_input")
      )

      Spacer(modifier = Modifier.height(10.dp))

      // ─────────────────────────────────────────────────────────────
      // 3. CATEGORY FILTERS: All, Men's Salon, Women's Beauty Parlour, Unisex Salon
      // ─────────────────────────────────────────────────────────────
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .testTag("category_filter_row"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        SalonCategoryFilter.values().forEach { filter ->
          val isSelected = selectedCategory == filter
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(
                if (isSelected) GoldPrimary else LuxurySurface
              )
              .border(
                width = 1.dp,
                color = if (isSelected) GoldBorder else DarkBorder,
                shape = RoundedCornerShape(20.dp)
              )
              .clickable { selectedCategory = filter }
              .padding(horizontal = 14.dp, vertical = 7.dp)
              .testTag("filter_chip_${filter.testId}"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = filter.displayName,
              fontFamily = MontserratFamily,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              fontSize = 11.5.sp,
              color = if (isSelected) Color(0xFF140E02) else TextSecondary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // ─────────────────────────────────────────────────────────────
      // 4. SALON CARDS LISTING
      // ─────────────────────────────────────────────────────────────
      if (filteredSalons.isEmpty()) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .testTag("no_salons_card"),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = LuxurySurface),
          border = BorderStroke(1.dp, DarkBorder)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = null,
              tint = TextMuted,
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "No salons found matching your filter.",
              fontFamily = MontserratFamily,
              fontWeight = FontWeight.SemiBold,
              fontSize = 13.5.sp,
              color = TextWhite,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Try selecting \"All (Men & Women)\" to view all available salons.",
              fontFamily = MontserratFamily,
              fontSize = 11.5.sp,
              color = TextMuted,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
              onClick = {
                selectedCategory = SalonCategoryFilter.ALL
                searchQuery = ""
              },
              colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("btn_reset_filter_all")
            ) {
              Text(
                text = "Show All Salons",
                fontFamily = MontserratFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color(0xFF1E1302)
              )
            }
          }
        }
      } else {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          filteredSalons.forEach { salon ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onSalonSelected(salon) }
                .testTag("salon_listing_card_${salon.salonId}"),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = LuxurySurface),
              border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.45f))
            ) {
              Column {
                // Salon Banner / Poster Preview
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
                    .background(LuxuryBlackElevated)
                ) {
                  val bUri = salon.bannerUri
                  var bBitmap by remember(bUri) {
                    mutableStateOf<android.graphics.Bitmap?>(null)
                  }
                  if (bUri != null) {
                    remember(bUri) {
                      try {
                        val parsed = Uri.parse(bUri)
                        if (parsed.scheme == "file") {
                          val f = File(parsed.path ?: "")
                          if (f.exists()) {
                            bBitmap = BitmapFactory.decodeFile(f.absolutePath)
                          }
                        } else {
                          context.contentResolver.openInputStream(parsed)?.use { s ->
                            bBitmap = BitmapFactory.decodeStream(s)
                          }
                        }
                      } catch (e: Exception) {
                        bBitmap = null
                      }
                      true
                    }
                  }

                  val loaded = bBitmap
                  if (loaded != null) {
                    Image(
                      bitmap = loaded.asImageBitmap(),
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

                  // Top badges row: Status (Left) & Distance + Rating (Right)
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    // Status Pill (ACTIVE in Green / DEACTIVATED in Red)
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                          if (salon.isActive) Color(0xFF0C2414).copy(alpha = 0.92f)
                          else Color(0xFF2B0A0D).copy(alpha = 0.92f)
                        )
                        .border(
                          0.5.dp,
                          if (salon.isActive) Color(0xFF4CAF50) else Color(0xFFFF5252),
                          RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                        .testTag("portal_salon_status_badge_${salon.salonId}")
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                          modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (salon.isActive) Color(0xFF4CAF50) else Color(0xFFFF5252))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                          text = if (salon.isActive) "ACTIVE" else "DEACTIVATED",
                          fontFamily = MontserratFamily,
                          fontWeight = FontWeight.Bold,
                          fontSize = 10.sp,
                          color = if (salon.isActive) Color(0xFF4CAF50) else Color(0xFFFF5252)
                        )
                      }
                    }

                    // Right Side: Distance badge & Rating badge
                    Row(
                      horizontalArrangement = Arrangement.spacedBy(6.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      // Distance Chip
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(6.dp))
                          .background(LuxuryBlack.copy(alpha = 0.88f))
                          .border(0.5.dp, GoldBorder.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                          .padding(horizontal = 7.dp, vertical = 3.dp)
                          .testTag("salon_distance_${salon.salonId}")
                      ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Icon(
                            imageVector = Icons.Default.NearMe,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(10.dp)
                          )
                          Spacer(modifier = Modifier.width(3.dp))
                          Text(
                            text = formatSalonDistance(salon),
                            fontFamily = MontserratFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.5.sp,
                            color = GoldAccent
                          )
                        }
                      }

                      // Rating Pill
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(6.dp))
                          .background(LuxuryBlack.copy(alpha = 0.88f))
                          .border(0.5.dp, GoldBorder, RoundedCornerShape(6.dp))
                          .padding(horizontal = 7.dp, vertical = 3.dp)
                          .testTag("salon_rating_${salon.salonId}")
                      ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(11.dp)
                          )
                          Spacer(modifier = Modifier.width(2.dp))
                          Text(
                            text = "${salon.rating}",
                            fontFamily = MontserratFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = GoldPrimary
                          )
                        }
                      }
                    }
                  }
                }

                // Info details
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = salon.salonName,
                      fontFamily = MontserratFamily,
                      fontWeight = FontWeight.Bold,
                      fontSize = 16.sp,
                      color = TextWhite,
                      modifier = Modifier
                        .weight(1f)
                        .testTag("salon_name_${salon.salonId}")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = salon.businessType,
                      fontFamily = MontserratFamily,
                      fontWeight = FontWeight.Medium,
                      fontSize = 11.sp,
                      color = PinkPrimary,
                      modifier = Modifier.testTag("salon_type_${salon.salonId}")
                    )
                  }

                  Spacer(modifier = Modifier.height(4.dp))

                  // Horizontal Partner Status Text Row
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("salon_status_row_${salon.salonId}")
                  ) {
                    Box(
                      modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (salon.isActive) Color(0xFF4CAF50) else Color(0xFFFF5252))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = if (salon.isActive) "ACTIVE • Accepting Bookings/Orders" else "DEACTIVATED • Currently Unavailable",
                      fontFamily = MontserratFamily,
                      fontWeight = FontWeight.SemiBold,
                      fontSize = 11.sp,
                      color = if (salon.isActive) Color(0xFF4CAF50) else Color(0xFFFF5252),
                      modifier = Modifier.testTag("salon_active_status_text_${salon.salonId}")
                    )
                  }

                  Spacer(modifier = Modifier.height(6.dp))

                  // Address & Distance info
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.LocationOn,
                      contentDescription = null,
                      tint = GoldPrimary,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = if (!salon.address.isNullOrBlank()) "${salon.address} (${formatSalonDistance(salon)})"
                             else "San Francisco (${formatSalonDistance(salon)})",
                      fontFamily = MontserratFamily,
                      fontSize = 11.5.sp,
                      color = TextIvory,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                      modifier = Modifier.testTag("salon_address_${salon.salonId}")
                    )
                  }

                  Spacer(modifier = Modifier.height(4.dp))

                  // Contact
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Phone,
                      contentDescription = null,
                      tint = TextSecondary,
                      modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "Phone: ${salon.mobileNumber}",
                      fontFamily = MontserratFamily,
                      fontSize = 11.sp,
                      color = TextSecondary,
                      modifier = Modifier.testTag("salon_phone_${salon.salonId}")
                    )
                  }

                  // GPS indicator
                  if (salon.latitude != null && salon.longitude != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "GPS: ${salon.latitude}, ${salon.longitude}",
                      fontFamily = MontserratFamily,
                      fontSize = 10.5.sp,
                      color = GoldAccent,
                      modifier = Modifier.testTag("salon_gps_${salon.salonId}")
                    )
                  }

                  Spacer(modifier = Modifier.height(12.dp))

                  // Action Buttons: View Salon & Directions
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Button(
                      onClick = { onSalonSelected(salon) },
                      colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("btn_view_salon_${salon.salonId}")
                    ) {
                      Text(
                        text = "VIEW SALON",
                        fontFamily = MontserratFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF1E1302)
                      )
                    }

                    OutlinedButton(
                      onClick = { openGoogleMapsDirections(context, salon) },
                      border = BorderStroke(1.dp, GoldBorder),
                      shape = RoundedCornerShape(8.dp),
                      colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                      modifier = Modifier
                        .height(38.dp)
                        .testTag("btn_get_directions_${salon.salonId}")
                    ) {
                      Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = GoldPrimary
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        text = "Directions",
                        fontFamily = MontserratFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(26.dp))

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
