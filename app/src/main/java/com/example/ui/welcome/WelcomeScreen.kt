package com.example.ui.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
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
import com.example.ui.components.CustomerCardPhoto
import com.example.ui.components.HeroSalonPhotoBanner
import com.example.ui.components.OwnerCardPhoto
import com.example.ui.components.SalonMateLogoBadge
import com.example.ui.theme.DividerGoldPink
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldGradientEnd
import com.example.ui.theme.GoldGradientStart
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldTagBg
import com.example.ui.theme.LuxuryBlack
import com.example.ui.theme.MontserratFamily
import com.example.ui.theme.PinkBorder
import com.example.ui.theme.PinkDeep
import com.example.ui.theme.PinkGlow
import com.example.ui.theme.PinkGradientEnd
import com.example.ui.theme.PinkGradientStart
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.PinkTagBg
import com.example.ui.theme.TextIvory
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextWhite

@Composable
fun WelcomeScreen(
  modifier: Modifier = Modifier,
  onCustomerClick: () -> Unit = {},
  onOwnerClick: () -> Unit = {},
  onAboutClick: () -> Unit = {},
  onSupportClick: () -> Unit = {},
  onPrivacyClick: () -> Unit = {}
) {
  val scrollState = rememberScrollState()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(LuxuryBlack)
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("welcome_screen_root"),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 560.dp)
        .verticalScroll(scrollState),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // 1. Hero Area: Visual characters + Floating Logo Badge
      HeroSection()

      Spacer(modifier = Modifier.height(8.dp))

      // 2. SalonMate Branding Header & Subtitle
      BrandHeaderSection()

      Spacer(modifier = Modifier.height(16.dp))

      // 3. Three Compact Feature Badges Row
      FeatureBadgesRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      )

      Spacer(modifier = Modifier.height(18.dp))

      // 4. Customer Portal Card
      CustomerPortalCard(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        onContinueClick = onCustomerClick
      )

      Spacer(modifier = Modifier.height(16.dp))

      // 5. Salon / Parlour Owner Card
      OwnerPortalCard(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        onContinueClick = onOwnerClick
      )

      Spacer(modifier = Modifier.height(24.dp))

      // 6. Footer Links
      FooterLinksSection(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        onAboutClick = onAboutClick,
        onSupportClick = onSupportClick,
        onPrivacyClick = onPrivacyClick
      )

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

/**
 * 1. Hero Section:
 * Male barber styling male client on left, female beauty professional on right,
 * with original SalonMate emblem logo floating centrally.
 */
@Composable
private fun HeroSection() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(240.dp),
    contentAlignment = Alignment.Center
  ) {
    // Realistic photographic salon banner (male barber left, female cucumber facial right)
    HeroSalonPhotoBanner(
      modifier = Modifier.fillMaxSize(),
      height = 240.dp
    )

    // Original SalonMate Emblem Logo Badge centered between characters
    SalonMateLogoBadge(
      modifier = Modifier
        .align(Alignment.Center)
        .testTag("salonmate_logo_badge")
    )
  }
}

/**
 * 2. Brand Header:
 * "SalonMate" title ("Salon" in gold, "Mate" in hot pink)
 * "BOOK • BEAUTY • BE YOU"
 * "Men Salon | Women Beauty Parlour"
 */
@Composable
private fun BrandHeaderSection() {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.padding(horizontal = 16.dp)
  ) {
    // "SALONMATE" Main Title matching reference
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "SALON",
        fontSize = 36.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = MontserratFamily,
        color = GoldPrimary,
        letterSpacing = 1.sp
      )
      Text(
        text = "MATE",
        fontSize = 36.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = MontserratFamily,
        color = PinkPrimary,
        letterSpacing = 1.sp
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    // "BOOK • BEAUTY • BE YOU"
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Text(
        text = "BOOK",
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        color = TextWhite,
        fontFamily = MontserratFamily
      )
      Text(
        text = "  •  ",
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        color = GoldPrimary
      )
      Text(
        text = "BEAUTY",
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        color = TextWhite,
        fontFamily = MontserratFamily
      )
      Text(
        text = "  •  ",
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        color = GoldPrimary
      )
      Text(
        text = "BE YOU",
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        color = TextWhite,
        fontFamily = MontserratFamily
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    // "Men Salon | Women Beauty Parlour"
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Text(
        text = "Men Salon",
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Medium,
        color = TextIvory,
        fontFamily = MontserratFamily
      )
      Text(
        text = "   |   ",
        fontSize = 14.sp,
        fontWeight = FontWeight.Light,
        color = GoldPrimary.copy(alpha = 0.7f)
      )
      Text(
        text = "Women Beauty Parlour",
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Medium,
        color = TextIvory,
        fontFamily = MontserratFamily
      )
    }
  }
}

/**
 * 3. Feature Badges:
 * Verified Salons & Parlours, Easy Booking, Trusted Professionals.
 */
@Composable
private fun FeatureBadgesRow(
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    FeatureBadgeItem(
      iconRes = SalonMateAssets.shieldCheckIcon,
      label = stringResource(R.string.badge_verified),
      modifier = Modifier.weight(1f)
    )
    FeatureBadgeItem(
      iconRes = SalonMateAssets.calendarBookingIcon,
      label = stringResource(R.string.badge_easy_booking),
      modifier = Modifier.weight(1f)
    )
    FeatureBadgeItem(
      iconRes = SalonMateAssets.trustedStarIcon,
      label = stringResource(R.string.badge_trusted),
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun FeatureBadgeItem(
  iconRes: Int,
  label: String,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(22.dp)

  Box(
    modifier = modifier
      .clip(shape)
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color(0xFF1E0E22),
            Color(0xFF140818)
          )
        )
      )
      .border(
        width = 1.2.dp,
        brush = Brush.linearGradient(
          colors = listOf(
            PinkBorder,
            Color(0xFFFF3388),
            PinkBorder
          )
        ),
        shape = shape
      )
      .padding(horizontal = 6.dp, vertical = 7.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(24.dp)
          .clip(CircleShape)
          .background(PinkDeep.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          painter = painterResource(id = iconRes),
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(14.dp)
        )
      }

      Spacer(modifier = Modifier.width(6.dp))

      Text(
        text = label,
        fontSize = 10.5.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 13.sp,
        color = TextWhite,
        fontFamily = MontserratFamily
      )
    }
  }
}

/**
 * 4. Customer Portal Card:
 * Outer glowing hot-pink card, user icon, "CUSTOMER PORTAL" tag,
 * "CUSTOMER" title, "Continue as Customer →" button,
 * male cape + female cucumber art, and 7-category quick strip.
 */
@Composable
private fun CustomerPortalCard(
  modifier: Modifier = Modifier,
  onContinueClick: () -> Unit
) {
  val cardShape = RoundedCornerShape(20.dp)

  Card(
    modifier = modifier
      .shadow(
        elevation = 12.dp,
        shape = cardShape,
        ambientColor = PinkGlow,
        spotColor = PinkPrimary.copy(alpha = 0.4f)
      )
      .border(
        border = BorderStroke(
          width = 1.5.dp,
          brush = Brush.linearGradient(
            colors = listOf(
              PinkBorder,
              Color(0xFFFF3399),
              PinkBorder.copy(alpha = 0.8f)
            )
          )
        ),
        shape = cardShape
      ),
    shape = cardShape,
    colors = CardDefaults.cardColors(
      containerColor = Color(0xFF170B1F)
    ),
    onClick = onContinueClick
  ) {
    Column {
      // Top row: Info & Action on left, Character art on right
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp, top = 14.dp, end = 8.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left Column: Text & CTA button
        Column(
          modifier = Modifier.weight(1.3f)
        ) {
          // Tag + Icon Header
          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Pink Rounded User Badge
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                  brush = Brush.linearGradient(
                    listOf(PinkPrimary, PinkDeep)
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                painter = painterResource(id = SalonMateAssets.userSearchIcon),
                contentDescription = stringResource(R.string.content_desc_customer_badge),
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Pill tag
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(PinkTagBg)
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = stringResource(R.string.customer_portal_tag),
                color = Color(0xFFFF66A8),
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.4.sp,
                fontFamily = MontserratFamily
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // "CUSTOMER" Title
          Text(
            text = stringResource(R.string.customer_portal_title),
            color = TextWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = MontserratFamily
          )

          Spacer(modifier = Modifier.height(2.dp))

          // Subtitle
          Text(
            text = stringResource(R.string.customer_portal_desc),
            color = TextSecondary,
            fontSize = 11.5.sp,
            lineHeight = 15.sp,
            fontFamily = MontserratFamily
          )

          Spacer(modifier = Modifier.height(10.dp))

          // "Continue as Customer →" Gradient Button
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(50.dp))
              .background(
                brush = Brush.horizontalGradient(
                  colors = listOf(
                    PinkGradientStart,
                    PinkPrimary,
                    PinkGradientEnd
                  )
                )
              )
              .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.White),
                onClick = onContinueClick
              )
              .padding(horizontal = 16.dp, vertical = 9.dp)
              .testTag("continue_as_customer_button"),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = stringResource(R.string.btn_continue_customer),
                color = Color.White,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = MontserratFamily
              )
              Spacer(modifier = Modifier.width(6.dp))
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.content_desc_arrow),
                tint = Color.White,
                modifier = Modifier.size(15.dp)
              )
            }
          }
        }

        // Right Photo: Male customer in cape + female facial customer
        Box(
          modifier = Modifier
            .weight(1.0f)
            .height(148.dp),
          contentAlignment = Alignment.Center
        ) {
          CustomerCardPhoto(modifier = Modifier.fillMaxSize())
        }
      }

      // Bottom Section: 7-item quick service category access
      CustomerServicesStrip()
    }
  }
}

/**
 * 7 Service Quick Category items inside Customer Card:
 * Haircut & Styling, Facial & Skin Care, Makeup & Beauty,
 * Nail Care, Spa & Relaxation, Beard & Grooming, More Services.
 */
@Composable
private fun CustomerServicesStrip() {
  val horizontalScroll = rememberScrollState()

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFF0E0613))
      .border(
        width = 0.8.dp,
        color = Color(0xFF2E132D)
      )
      .padding(vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(horizontalScroll)
        .padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      ServiceStripItem(
        iconRes = SalonMateAssets.scissorsIcon,
        title = stringResource(R.string.service_haircut)
      )
      StripDivider()

      ServiceStripItem(
        iconRes = SalonMateAssets.facialIcon,
        title = stringResource(R.string.service_facial)
      )
      StripDivider()

      ServiceStripItem(
        iconRes = SalonMateAssets.makeupIcon,
        title = stringResource(R.string.service_makeup)
      )
      StripDivider()

      ServiceStripItem(
        iconRes = SalonMateAssets.nailIcon,
        title = stringResource(R.string.service_nail)
      )
      StripDivider()

      ServiceStripItem(
        iconRes = SalonMateAssets.spaIcon,
        title = stringResource(R.string.service_spa)
      )
      StripDivider()

      ServiceStripItem(
        iconRes = SalonMateAssets.beardIcon,
        title = stringResource(R.string.service_beard)
      )
      StripDivider()

      ServiceStripItem(
        iconRes = SalonMateAssets.moreIcon,
        title = stringResource(R.string.service_more)
      )
    }
  }
}

@Composable
private fun ServiceStripItem(
  iconRes: Int,
  title: String
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .padding(horizontal = 8.dp)
      .width(58.dp)
  ) {
    Icon(
      painter = painterResource(id = iconRes),
      contentDescription = title,
      tint = PinkPrimary,
      modifier = Modifier.size(19.dp)
    )
    Spacer(modifier = Modifier.height(3.dp))
    Text(
      text = title,
      fontSize = 8.5.sp,
      fontWeight = FontWeight.Medium,
      lineHeight = 11.sp,
      color = TextSecondary,
      textAlign = TextAlign.Center,
      fontFamily = MontserratFamily
    )
  }
}

@Composable
private fun StripDivider() {
  Box(
    modifier = Modifier
      .width(1.dp)
      .height(24.dp)
      .background(Color(0xFF33152F))
  )
}

/**
 * 5. Salon / Parlour Owner Card:
 * Outer glowing golden card, shopfront icon, "BUSINESS PORTAL" tag,
 * "SALON / PARLOUR OWNER" title, "Continue as Salon / Parlour Owner →" button,
 * and professional salon workers in uniforms art.
 */
@Composable
private fun OwnerPortalCard(
  modifier: Modifier = Modifier,
  onContinueClick: () -> Unit
) {
  val cardShape = RoundedCornerShape(20.dp)

  Card(
    modifier = modifier
      .shadow(
        elevation = 12.dp,
        shape = cardShape,
        ambientColor = GoldPrimary.copy(alpha = 0.3f),
        spotColor = GoldAccent.copy(alpha = 0.4f)
      )
      .border(
        border = BorderStroke(
          width = 1.5.dp,
          brush = Brush.linearGradient(
            colors = listOf(
              GoldBorder,
              GoldPrimary,
              GoldAccent
            )
          )
        ),
        shape = cardShape
      ),
    shape = cardShape,
    colors = CardDefaults.cardColors(
      containerColor = Color(0xFF19120A)
    ),
    onClick = onContinueClick
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, top = 14.dp, end = 8.dp, bottom = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left Column: Text & CTA button
      Column(
        modifier = Modifier.weight(1.3f)
      ) {
        // Tag + Icon Header
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Gold Rounded Shop Badge
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(
                brush = Brush.linearGradient(
                  listOf(GoldPrimary, GoldAccent)
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              painter = painterResource(id = SalonMateAssets.storefrontIcon),
              contentDescription = stringResource(R.string.content_desc_owner_badge),
              tint = Color(0xFF1F1504),
              modifier = Modifier.size(19.dp)
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          // Pill tag
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(GoldTagBg)
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = stringResource(R.string.owner_portal_tag),
              color = GoldPrimary,
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.4.sp,
              fontFamily = MontserratFamily
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // "SALON / PARLOUR OWNER" Title
        Text(
          text = stringResource(R.string.owner_portal_title),
          color = TextWhite,
          fontSize = 18.5.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = MontserratFamily
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Subtitle
        Text(
          text = stringResource(R.string.owner_portal_desc),
          color = Color(0xFFDFD7CA),
          fontSize = 11.5.sp,
          lineHeight = 15.sp,
          fontFamily = MontserratFamily
        )

        Spacer(modifier = Modifier.height(10.dp))

        // "Continue as Salon / Parlour Owner →" Warm Gold Gradient Button
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(
              brush = Brush.horizontalGradient(
                colors = listOf(
                  GoldGradientStart,
                  GoldPrimary,
                  GoldAccent
                )
              )
            )
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = ripple(color = Color(0xFF261904)),
              onClick = onContinueClick
            )
            .padding(horizontal = 14.dp, vertical = 9.dp)
            .testTag("continue_as_owner_button"),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = stringResource(R.string.btn_continue_owner),
              color = Color(0xFF1D1303),
              fontSize = 11.5.sp,
              fontWeight = FontWeight.ExtraBold,
              fontFamily = MontserratFamily
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = stringResource(R.string.content_desc_arrow),
              tint = Color(0xFF1D1303),
              modifier = Modifier.size(15.dp)
            )
          }
        }
      }

      // Right Photo: Professional salon workers in uniforms
      Box(
        modifier = Modifier
          .weight(1.0f)
          .height(148.dp),
        contentAlignment = Alignment.Center
      ) {
        OwnerCardPhoto(modifier = Modifier.fillMaxSize())
      }
    }
  }
}

/**
 * 6. Footer Links:
 * About SalonMate | Help & Support | Privacy Policy
 */
@Composable
private fun FooterLinksSection(
  modifier: Modifier = Modifier,
  onAboutClick: () -> Unit,
  onSupportClick: () -> Unit,
  onPrivacyClick: () -> Unit
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    FooterLinkItem(
      title = stringResource(R.string.footer_about),
      icon = Icons.Outlined.Info,
      testTag = "footer_about_salonmate",
      onClick = onAboutClick
    )

    Text(
      text = "|",
      color = Color(0xFF4A4252),
      fontSize = 13.sp,
      modifier = Modifier.padding(horizontal = 8.dp)
    )

    FooterLinkItem(
      title = stringResource(R.string.footer_support),
      icon = Icons.Outlined.HelpOutline,
      testTag = "footer_help_support",
      onClick = onSupportClick
    )

    Text(
      text = "|",
      color = Color(0xFF4A4252),
      fontSize = 13.sp,
      modifier = Modifier.padding(horizontal = 8.dp)
    )

    FooterLinkItem(
      title = stringResource(R.string.footer_privacy),
      icon = Icons.Outlined.Shield,
      testTag = "footer_privacy_policy",
      onClick = onPrivacyClick
    )
  }
}

@Composable
private fun FooterLinkItem(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  testTag: String,
  onClick: () -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(color = GoldPrimary),
        onClick = onClick
      )
      .padding(horizontal = 4.dp, vertical = 6.dp)
      .testTag(testTag)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = title,
      tint = TextSecondary,
      modifier = Modifier.size(13.dp)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = title,
      color = TextSecondary,
      fontSize = 11.5.sp,
      fontWeight = FontWeight.Medium,
      fontFamily = MontserratFamily
    )
  }
}
