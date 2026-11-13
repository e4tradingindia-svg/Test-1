package com.example.ui.screens.customer

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.SalonEntity
import com.example.data.repository.CustomerRepository
import com.example.data.repository.SalonRepository
import com.example.data.session.SessionManager
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerPortalScreen(
    customerId: String,
    customerRepository: CustomerRepository,
    salonRepository: SalonRepository,
    sessionManager: SessionManager,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToSalonDetails: (String) -> Unit,
    onLogout: () -> Unit
) {
    val customer by customerRepository.observeCustomer(customerId).collectAsState(initial = null)
    val allSalons by salonRepository.observeAllSalons().collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All (Men & Women)") }

    val categoryFilters = listOf(
        "All (Men & Women)",
        "Men's Salon",
        "Women's Beauty Parlour",
        "Unisex Salon"
    )

    // Filter salons
    val filteredSalons = allSalons.filter { salon ->
        val matchesCategory = when (selectedCategoryFilter) {
            "Men's Salon" -> salon.businessType.equals("Men's Salon", ignoreCase = true)
            "Women's Beauty Parlour" -> salon.businessType.equals("Women's Beauty Parlour", ignoreCase = true)
            "Unisex Salon" -> salon.businessType.equals("Unisex Salon", ignoreCase = true)
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() ||
                salon.salonName.contains(searchQuery, ignoreCase = true) ||
                salon.address.contains(searchQuery, ignoreCase = true) ||
                salon.businessType.contains(searchQuery, ignoreCase = true)

        matchesCategory && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.salonmate_original_logo),
                            contentDescription = "SalonMate Logo",
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SALONMATE",
                            fontFamily = CinzelFamily,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary,
                            fontSize = 20.sp,
                            letterSpacing = 2.sp
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            sessionManager.clearCustomerSession()
                            onLogout()
                        },
                        modifier = Modifier.testTag("btn_customer_logout")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = GoldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackBackground)
            )
        },
        containerColor = BlackBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Customer Header Card with Edit Profile Pencil
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(LuxuryCardBg)
                        .border(1.dp, GoldBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(LuxurySurface)
                                    .border(1.5.dp, GoldPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (customer?.profilePhotoUri?.isNotBlank() == true) {
                                    AsyncImage(
                                        model = customer?.profilePhotoUri,
                                        contentDescription = "Profile Photo",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = GoldSecondary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Hello, ${customer?.name ?: "Valued Customer"}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "ID: ${customer?.customerId ?: customerId}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldLight
                                )
                                if (customer?.address?.isNotBlank() == true) {
                                    Text(
                                        text = customer?.address ?: "",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Pencil Edit Button
                        IconButton(
                            onClick = onNavigateToEditProfile,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(PinkPrimary.copy(alpha = 0.15f))
                                .border(1.dp, PinkPrimary.copy(alpha = 0.4f), CircleShape)
                                .size(40.dp)
                                .testTag("btn_edit_profile_pencil")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = PinkPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search salons, parlours, or area...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_salon_search"),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = LuxuryCardBg,
                        unfocusedContainerColor = LuxuryCardBg,
                        focusedIndicatorColor = GoldPrimary,
                        unfocusedIndicatorColor = GoldBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            // Compact Explore Services Labels (Requirement 1: smaller, compact, simple non-clickable service labels/icons, not button-like)
            item {
                Column {
                    Text(
                        text = "Explore Services",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val servicesList = listOf(
                        "Haircut & Styling" to Icons.Default.Face,
                        "Beard Grooming" to Icons.Default.Face,
                        "Facial & Glow" to Icons.Default.Face,
                        "Luxury Spa" to Icons.Default.Spa,
                        "Nail Art & Care" to Icons.Default.Star,
                        "Bridal & Glam" to Icons.Default.Star
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        servicesList.take(3).forEach { (name, iconVector) ->
                            CompactServiceLabel(
                                title = name,
                                icon = iconVector,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        servicesList.drop(3).forEach { (name, iconVector) ->
                            CompactServiceLabel(
                                title = name,
                                icon = iconVector,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Category Filters (Requirement 3 & 4)
            item {
                Column {
                    Text(
                        text = "Category Filter",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(categoryFilters) { filter ->
                            val isSelected = selectedCategoryFilter == filter
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategoryFilter = filter },
                                label = {
                                    Text(
                                        text = filter,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldPrimary,
                                    selectedLabelColor = BlackBackground,
                                    containerColor = LuxuryCardBg,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = GoldBorder,
                                    selectedBorderColor = GoldPrimary,
                                    enabled = true,
                                    selected = isSelected
                                ),
                                modifier = Modifier.testTag("filter_chip_${filter.replace(" ", "_")}")
                            )
                        }
                    }
                }
            }

            // Nearby Salons Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nearby Salons & Parlours",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = CinzelFamily,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    Text(
                        text = "${filteredSalons.size} found",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }

            // Salon Cards
            if (filteredSalons.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No salons match your search or filter",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(filteredSalons, key = { it.salonId }) { salon ->
                    SalonCard(
                        salon = salon,
                        onCardClick = { onNavigateToSalonDetails(salon.salonId) }
                    )
                }
            }
        }
    }
}

// Compact non-clickable label/icon (not a button)
@Composable
private fun CompactServiceLabel(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(LuxurySurface)
            .border(0.5.dp, GoldBorder.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GoldSecondary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SalonCard(
    salon: SalonEntity,
    onCardClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LuxuryCardBg)
            .border(1.dp, GoldBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onCardClick)
            .testTag("salon_card_${salon.salonId}")
    ) {
        Column {
            // Poster / Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                if (salon.bannerUri.isNotBlank()) {
                    AsyncImage(
                        model = salon.bannerUri,
                        contentDescription = "${salon.salonName} Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback banner
                    val fallbackRes = when (salon.businessType) {
                        "Men's Salon" -> R.drawable.salonmate_men_hero
                        "Women's Beauty Parlour" -> R.drawable.salonmate_women_hero
                        else -> R.drawable.hero_salon_banner
                    }
                    Image(
                        painter = painterResource(id = fallbackRes),
                        contentDescription = "Salon Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Active / Deactivated Badge on top right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (salon.isActive) ActiveGreenBg else DeactivatedRedBg)
                        .border(
                            1.dp,
                            if (salon.isActive) ActiveGreen else DeactivatedRed,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (salon.isActive) "ACTIVE" else "DEACTIVATED",
                        color = if (salon.isActive) ActiveGreen else DeactivatedRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Business Type Badge bottom left
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BlackBackground.copy(alpha = 0.85f))
                        .border(0.5.dp, GoldBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = salon.businessType,
                        color = GoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Info Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = salon.salonName,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = CinzelFamily,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = String.format("%.1f", salon.rating),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        Text(
                            text = "(${salon.reviewCount})",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = PinkPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = salon.address,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
