package com.example.ui.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
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

@Immutable
private data class WelcomeLayoutMetrics(
  val heroHeight: Dp,
  val logoSize: Dp,
  val titleFontSize: TextUnit,
  val taglineFontSize: TextUnit,
  val taglineLetterSpacing: TextUnit,
  val subtitleFontSize: TextUnit,
  val afterHeroSpace: Dp,
  val afterBrandSpace: Dp,
  val afterBadgesSpace: Dp,
  val betweenCardsSpace: Dp,
  val beforeFooterSpace: Dp,
  val bottomSpace: Dp,
  val badgeHorizontalPadding: Dp,
  val badgeVerticalPadding: Dp,
  val badgeIconBoxSize: Dp,
  val badgeIconSize: Dp,
  val badgeFontSize: TextUnit,
  val badgeLineHeight: TextUnit,
  val cardCornerRadius: Dp,
  val cardPadStart: Dp,
  val cardPadTop: Dp,
  val cardPadEnd: Dp,
  val cardPadBottom: Dp,
  val cardBadgeSize: Dp,
  val cardBadgeIconSize: Dp,
  val cardTagFontSize: TextUnit,
  val cardTagPadH: Dp,
  val cardTagPadV: Dp,
  val customerTitleSize: TextUnit,
  val ownerTitleSize: TextUnit,
  val cardDescFontSize: TextUnit,
  val cardDescLineHeight: TextUnit,
  val cardTitleTopSpace: Dp,
  val cardButtonTopSpace: Dp,
  val cardButtonPadH: Dp,
  val cardButtonPadV: Dp,
  val customerButtonFontSize: TextUnit,
  val ownerButtonFontSize: TextUnit,
  val cardPhotoHeight: Dp,
  val stripPadV: Dp,
  val stripIconSize: Dp,
  val stripTextSize: TextUnit,
  val stripLineHeight: TextUnit,
  val stripItemWidth: Dp,
  val stripDividerHeight: Dp,
  val footerPadV: Dp,
  val footerFontSize: TextUnit,
  val footerIconSize: Dp
)

private fun rememberWelcomeLayoutMetrics(availableHeight: Dp): WelcomeLayoutMetrics {
  return when {
    availableHeight < 650.dp -> {
      val heroHeight = (availableHeight * 0.175f).coerceIn(96.dp, 118.dp)
      val logoSize = (heroHeight * 0.72f).coerceIn(70.dp, 88.dp)
      WelcomeLayoutMetrics(
        heroHeight = heroHeight,
        logoSize = logoSize,
        titleFontSize = 24.sp,
        taglineFontSize = 9.5.sp,
        taglineLetterSpacing = 1.5.sp,
        subtitleFontSize = 11.sp,
        afterHeroSpace = 4.dp,
        afterBrandSpace = 6.dp,
        afterBadgesSpace = 6.dp,
        betweenCardsSpace = 7.dp,
        beforeFooterSpace = 6.dp,
        bottomSpace = 4.dp,
        badgeHorizontalPadding = 5.dp,
        badgeVerticalPadding = 4.dp,
        badgeIconBoxSize = 18.dp,
        badgeIconSize = 11.dp,
        badgeFontSize = 9.sp,
        badgeLineHeight = 11.sp,
        cardCornerRadius = 16.dp,
        cardPadStart = 12.dp,
        cardPadTop = 8.dp,
        cardPadEnd = 6.dp,
        cardPadBottom = 8.dp,
        cardBadgeSize = 26.dp,
        cardBadgeIconSize = 14.dp,
        cardTagFontSize = 8.5.sp,
        cardTagPadH = 7.dp,
        cardTagPadV = 2.dp,
        customerTitleSize = 17.sp,
        ownerTitleSize = 15.sp,
        cardDescFontSize = 10.sp,
        cardDescLineHeight = 12.5.sp,
        cardTitleTopSpace = 4.dp,
        cardButtonTopSpace = 6.dp,
        cardButtonPadH = 12.dp,
        cardButtonPadV = 6.dp,
        customerButtonFontSize = 11.sp,
        ownerButtonFontSize = 10.5.sp,
        cardPhotoHeight = (availableHeight * 0.15f).coerceIn(88.dp, 102.dp),
        stripPadV = 4.dp,
        stripIconSize = 15.dp,
        stripTextSize = 7.5.sp,
        stripLineHeight = 9.5.sp,
        stripItemWidth = 50.dp,
        stripDividerHeight = 18.dp,
        footerPadV = 3.dp,
        footerFontSize = 10.sp,
        footerIconSize = 11.dp
      )
    }
    availableHeight <= 780.dp -> {
      val heroHeight = (availableHeight * 0.19f).coerceIn(120.dp, 148.dp)
      val logoSize = (heroHeight * 0.72f).coerceIn(88.dp, 106.dp)
      WelcomeLayoutMetrics(
        heroHeight = heroHeight,
        logoSize = logoSize,
        titleFontSize = 27.sp,
        taglineFontSize = 10.5.sp,
        taglineLetterSpacing = 1.8.sp,
        subtitleFontSize = 12.sp,
        afterHeroSpace = 5.dp,
        afterBrandSpace = 9.dp,
        afterBadgesSpace = 9.dp,
        betweenCardsSpace = 10.dp,
        beforeFooterSpace = 10.dp,
        bottomSpace = 6.dp,
        badgeHorizontalPadding = 6.dp,
        badgeVerticalPadding = 5.dp,
        badgeIconBoxSize = 20.dp,
        badgeIconSize = 12.dp,
        badgeFontSize = 9.5.sp,
        badgeLineHeight = 11.5.sp,
        cardCornerRadius = 18.dp,
        cardPadStart = 14.dp,
        cardPadTop = 10.dp,
        cardPadEnd = 6.dp,
        cardPadBottom = 9.dp,
        cardBadgeSize = 28.dp,
        cardBadgeIconSize = 15.dp,
        cardTagFontSize = 9.sp,
        cardTagPadH = 8.dp,
        cardTagPadV = 3.dp,
        customerTitleSize = 19.sp,
        ownerTitleSize = 16.5.sp,
        cardDescFontSize = 10.5.sp,
        cardDescLineHeight = 13.5.sp,
        cardTitleTopSpace = 5.dp,
        cardButtonTopSpace = 7.dp,
        cardButtonPadH = 14.dp,
        cardButtonPadV = 7.dp,
        customerButtonFontSize = 11.5.sp,
        ownerButtonFontSize = 11.sp,
        cardPhotoHeight = (availableHeight * 0.16f).coerceIn(106.dp, 124.dp),
        stripPadV = 5.dp,
        stripIconSize = 17.dp,
        stripTextSize = 8.sp,
        stripLineHeight = 10.sp,
        stripItemWidth = 54.dp,
        stripDividerHeight = 20.dp,
        footerPadV = 4.dp,
        footerFontSize = 10.5.sp,
        footerIconSize = 12.dp
      )
    }
    else -> {
      val heroHeight = (availableHeight * 0.205f).coerceIn(152.dp, 190.dp)
      val logoSize = (heroHeight * 0.70f).coerceIn(106.dp, 126.dp)
      WelcomeLayoutMetrics(
        heroHeight = heroHeight,
        logoSize = logoSize,
        titleFontSize = 30.sp,
        taglineFontSize = 11.sp,
        taglineLetterSpacing = 2.sp,
        subtitleFontSize = 13.sp,
        afterHeroSpace = 6.dp,
        afterBrandSpace = 11.dp,
        afterBadgesSpace = 11.dp,
        betweenCardsSpace = 12.dp,
        beforeFooterSpace = 12.dp,
        bottomSpace = 8.dp,
        badgeHorizontalPadding = 6.dp,
        badgeVerticalPadding = 6.dp,
        badgeIconBoxSize = 22.dp,
        badgeIconSize = 13.dp,
        badgeFontSize = 10.sp,
        badgeLineHeight = 12.sp,
        cardCornerRadius = 18.dp,
        cardPadStart = 14.dp,
        cardPadTop = 11.dp,
        cardPadEnd = 8.dp,
        cardPadBottom = 10.dp,
        cardBadgeSize = 30.dp,
        cardBadgeIconSize = 16.dp,
        cardTagFontSize = 9.sp,
        cardTagPadH = 9.dp,
        cardTagPadV = 3.dp,
        customerTitleSize = 20.sp,
        ownerTitleSize = 17.5.sp,
        cardDescFontSize = 11.sp,
        cardDescLineHeight = 14.sp,
        cardTitleTopSpace = 6.dp,
        cardButtonTopSpace = 8.dp,
        cardButtonPadH = 14.dp,
        cardButtonPadV = 8.dp,
        customerButtonFontSize = 12.sp,
        ownerButtonFontSize = 11.5.sp,
        cardPhotoHeight = (availableHeight * 0.16f).coerceIn(120.dp, 140.dp),
        stripPadV = 6.dp,
        stripIconSize = 18.dp,
        stripTextSize = 8.5.sp,
        stripLineHeight = 10.5.sp,
        stripItemWidth = 56.dp,
        stripDividerHeight = 22.dp,
        footerPadV = 5.dp,
        footerFontSize = 11.sp,
        footerIconSize = 13.dp
      )
    }
  }
}

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

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(LuxuryBlack)
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("welcome_screen_root"),
    contentAlignment = Alignment.TopCenter
  ) {
    val metrics = rememberWelcomeLayoutMetrics(maxHeight)

    Column(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 560.dp)
        .verticalScroll(scrollState),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // 1. Hero Area: Visual characters + Floating Logo Badge
      HeroSection(metrics = metrics)

      Spacer(modifier = Modifier.height(metrics.afterHeroSpace))

      // 2. SalonMate Branding Header & Subtitle
      BrandHeaderSection(metrics = metrics)

      Spacer(modifier = Modifier.height(metrics.afterBrandSpace))

      // 3. Three Compact Feature Badges Row
      FeatureBadgesRow(
        metrics = metrics,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      )

      Spacer(modifier = Modifier.height(metrics.afterBadgesSpace))

      // 4. Customer Portal Card
      CustomerPortalCard(
        metrics = metrics,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        onContinueClick = onCustomerClick
      )

      Spacer(modifier = Modifier.height(metrics.betweenCardsSpace))

      // 5. Salon / Parlour Owner Card
      OwnerPortalCard(
        metrics = metrics,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        onContinueClick = onOwnerClick
      )

      Spacer(modifier = Modifier.height(metrics.beforeFooterSpace))

      // 6. Footer Links
      FooterLinksSection(
        metrics = metrics,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = metrics.footerPadV),
        onAboutClick = onAboutClick,
        onSupportClick = onSupportClick,
        onPrivacyClick = onPrivacyClick
      )

      Spacer(modifier = Modifier.height(metrics.bottomSpace))
    }
  }
}

/**
 * 1. Hero Section:
 * Male barber styling male client on left, female beauty professional on right,
 * with original SalonMate emblem logo floating centrally.
 */
@Composable
private fun HeroSection(metrics: WelcomeLayoutMetrics) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(metrics.heroHeight),
    contentAlignment = Alignment.Center
  ) {
    // Realistic photographic salon banner (male barber left, female cucumber facial right)
    HeroSalonPhotoBanner(
      modifier = Modifier.fillMaxSize(),
      height = metrics.heroHeight
    )

    // Original SalonMate Emblem Logo Badge centered between characters
    SalonMateLogoBadge(
      modifier = Modifier
        .align(Alignment.Center)
        .testTag("salonmate_logo_badge"),
      badgeWidth = metrics.logoSize,
      badgeHeight = metrics.logoSize
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
private fun BrandHeaderSection(metrics: WelcomeLayoutMetrics) {
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
        fontSize = metrics.titleFontSize,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = MontserratFamily,
        color = GoldPrimary,
        letterSpacing = 1.sp
      )
      Text(
        text = "MATE",
        fontSize = metrics.titleFontSize,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = MontserratFamily,
        color = PinkPrimary,
        letterSpacing = 1.sp
      )
    }

    Spacer(modifier = Modifier.height(2.dp))

    // "BOOK • BEAUTY • BE YOU"
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Text(
        text = "BOOK",
        fontSize = metrics.taglineFontSize,
        fontWeight = FontWeight.Bold,
        letterSpacing = metrics.taglineLetterSpacing,
        color = TextWhite,
        fontFamily = MontserratFamily
      )
      Text(
        text = "  •  ",
        fontSize = metrics.taglineFontSize,
        fontWeight = FontWeight.Black,
        color = GoldPrimary
      )
      Text(
        text = "BEAUTY",
        fontSize = metrics.taglineFontSize,
        fontWeight = FontWeight.Bold,
        letterSpacing = metrics.taglineLetterSpacing,
        color = TextWhite,
        fontFamily = MontserratFamily
      )
      Text(
        text = "  •  ",
        fontSize = metrics.taglineFontSize,
        fontWeight = FontWeight.Black,
        color = GoldPrimary
      )
      Text(
        text = "BE YOU",
        fontSize = metrics.taglineFontSize,
        fontWeight = FontWeight.Bold,
        letterSpacing = metrics.taglineLetterSpacing,
        color = TextWhite,
        fontFamily = MontserratFamily
      )
    }

    Spacer(modifier = Modifier.height(3.dp))

    // "Men Salon | Women Beauty Parlour"
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Text(
        text = "Men Salon",
        fontSize = metrics.subtitleFontSize,
        fontWeight = FontWeight.Medium,
        color = TextIvory,
        fontFamily = MontserratFamily
      )
      Text(
        text = "   |   ",
        fontSize = metrics.subtitleFontSize,
        fontWeight = FontWeight.Light,
        color = GoldPrimary.copy(alpha = 0.7f)
      )
      Text(
        text = "Women Beauty Parlour",
        fontSize = metrics.subtitleFontSize,
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
  metrics: WelcomeLayoutMetrics,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    FeatureBadgeItem(
      iconRes = SalonMateAssets.shieldCheckIcon,
      label = stringResource(R.string.badge_verified),
      metrics = metrics,
      modifier = Modifier.weight(1f)
    )
    FeatureBadgeItem(
      iconRes = SalonMateAssets.calendarBookingIcon,
      label = stringResource(R.string.badge_easy_booking),
      metrics = metrics,
      modifier = Modifier.weight(1f)
    )
    FeatureBadgeItem(
      iconRes = SalonMateAssets.trustedStarIcon,
      label = stringResource(R.string.badge_trusted),
      metrics = metrics,
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun FeatureBadgeItem(
  iconRes: Int,
  label: String,
  metrics: WelcomeLayoutMetrics,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(18.dp)

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
      .padding(horizontal = metrics.badgeHorizontalPadding, vertical = metrics.badgeVerticalPadding),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(metrics.badgeIconBoxSize)
          .clip(CircleShape)
          .background(PinkDeep.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          painter = painterResource(id = iconRes),
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(metrics.badgeIconSize)
        )
      }

      Spacer(modifier = Modifier.width(4.dp))

      Text(
        text = label,
        fontSize = metrics.badgeFontSize,
        fontWeight = FontWeight.SemiBold,
        lineHeight = metrics.badgeLineHeight,
        color = TextWhite,
        fontFamily = MontserratFamily,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
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
  metrics: WelcomeLayoutMetrics,
  modifier: Modifier = Modifier,
  onContinueClick: () -> Unit
) {
  val cardShape = RoundedCornerShape(metrics.cardCornerRadius)

  Card(
    modifier = modifier
      .shadow(
        elevation = 10.dp,
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
          .padding(
            start = metrics.cardPadStart,
            top = metrics.cardPadTop,
            end = metrics.cardPadEnd,
            bottom = metrics.cardPadBottom
          ),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left Column: Text & CTA button
        Column(
          modifier = Modifier.weight(1.35f)
        ) {
          // Tag + Icon Header
          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Pink Rounded User Badge
            Box(
              modifier = Modifier
                .size(metrics.cardBadgeSize)
                .clip(RoundedCornerShape(8.dp))
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
                modifier = Modifier.size(metrics.cardBadgeIconSize)
              )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Pill tag
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(PinkTagBg)
                .padding(horizontal = metrics.cardTagPadH, vertical = metrics.cardTagPadV)
            ) {
              Text(
                text = stringResource(R.string.customer_portal_tag),
                color = Color(0xFFFF66A8),
                fontSize = metrics.cardTagFontSize,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.4.sp,
                fontFamily = MontserratFamily
              )
            }
          }

          Spacer(modifier = Modifier.height(metrics.cardTitleTopSpace))

          // "CUSTOMER" Title
          Text(
            text = stringResource(R.string.customer_portal_title),
            color = TextWhite,
            fontSize = metrics.customerTitleSize,
            fontWeight = FontWeight.Bold,
            fontFamily = MontserratFamily
          )

          Spacer(modifier = Modifier.height(2.dp))

          // Subtitle
          Text(
            text = stringResource(R.string.customer_portal_desc),
            color = TextSecondary,
            fontSize = metrics.cardDescFontSize,
            lineHeight = metrics.cardDescLineHeight,
            fontFamily = MontserratFamily
          )

          Spacer(modifier = Modifier.height(metrics.cardButtonTopSpace))

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
              .padding(horizontal = metrics.cardButtonPadH, vertical = metrics.cardButtonPadV)
              .testTag("continue_as_customer_button"),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = stringResource(R.string.btn_continue_customer),
                color = Color.White,
                fontSize = metrics.customerButtonFontSize,
                fontWeight = FontWeight.Bold,
                fontFamily = MontserratFamily
              )
              Spacer(modifier = Modifier.width(5.dp))
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.content_desc_arrow),
                tint = Color.White,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }

        // Right Photo: Male customer in cape + female facial customer
        Box(
          modifier = Modifier
            .weight(0.95f)
            .height(metrics.cardPhotoHeight),
          contentAlignment = Alignment.Center
        ) {
          CustomerCardPhoto(modifier = Modifier.fillMaxSize())
        }
      }

      // Bottom Section: 7-item quick service category access
      CustomerServicesStrip(metrics = metrics)
    }
  }
}

/**
 * 7 Service Quick Category items inside Customer Card:
 * Haircut & Styling, Facial & Skin Care, Makeup & Beauty,
 * Nail Care, Spa & Relaxation, Beard & Grooming, More Services.
 */
@Composable
private fun CustomerServicesStrip(metrics: WelcomeLayoutMetrics) {
  val horizontalScroll = rememberScrollState()

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFF0E0613))
      .border(
        width = 0.8.dp,
        color = Color(0xFF2E132D)
      )
      .padding(vertical = metrics.stripPadV)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(horizontalScroll)
        .padding(horizontal = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      ServiceStripItem(
        iconRes = SalonMateAssets.scissorsIcon,
        title = stringResource(R.string.service_haircut),
        metrics = metrics
      )
      StripDivider(height = metrics.stripDividerHeight)

      ServiceStripItem(
        iconRes = SalonMateAssets.facialIcon,
        title = stringResource(R.string.service_facial),
        metrics = metrics
      )
      StripDivider(height = metrics.stripDividerHeight)

      ServiceStripItem(
        iconRes = SalonMateAssets.makeupIcon,
        title = stringResource(R.string.service_makeup),
        metrics = metrics
      )
      StripDivider(height = metrics.stripDividerHeight)

      ServiceStripItem(
        iconRes = SalonMateAssets.nailIcon,
        title = stringResource(R.string.service_nail),
        metrics = metrics
      )
      StripDivider(height = metrics.stripDividerHeight)

      ServiceStripItem(
        iconRes = SalonMateAssets.spaIcon,
        title = stringResource(R.string.service_spa),
        metrics = metrics
      )
      StripDivider(height = metrics.stripDividerHeight)

      ServiceStripItem(
        iconRes = SalonMateAssets.beardIcon,
        title = stringResource(R.string.service_beard),
        metrics = metrics
      )
      StripDivider(height = metrics.stripDividerHeight)

      ServiceStripItem(
        iconRes = SalonMateAssets.moreIcon,
        title = stringResource(R.string.service_more),
        metrics = metrics
      )
    }
  }
}

@Composable
private fun ServiceStripItem(
  iconRes: Int,
  title: String,
  metrics: WelcomeLayoutMetrics
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .padding(horizontal = 5.dp)
      .width(metrics.stripItemWidth)
  ) {
    Icon(
      painter = painterResource(id = iconRes),
      contentDescription = title,
      tint = PinkPrimary,
      modifier = Modifier.size(metrics.stripIconSize)
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = title,
      fontSize = metrics.stripTextSize,
      fontWeight = FontWeight.Medium,
      lineHeight = metrics.stripLineHeight,
      color = TextSecondary,
      textAlign = TextAlign.Center,
      fontFamily = MontserratFamily
    )
  }
}

@Composable
private fun StripDivider(height: Dp) {
  Box(
    modifier = Modifier
      .width(1.dp)
      .height(height)
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
  metrics: WelcomeLayoutMetrics,
  modifier: Modifier = Modifier,
  onContinueClick: () -> Unit
) {
  val cardShape = RoundedCornerShape(metrics.cardCornerRadius)

  Card(
    modifier = modifier
      .shadow(
        elevation = 10.dp,
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
        .padding(
          start = metrics.cardPadStart,
          top = metrics.cardPadTop,
          end = metrics.cardPadEnd,
          bottom = metrics.cardPadBottom
        ),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left Column: Text & CTA button
      Column(
        modifier = Modifier.weight(1.35f)
      ) {
        // Tag + Icon Header
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Gold Rounded Shop Badge
          Box(
            modifier = Modifier
              .size(metrics.cardBadgeSize)
              .clip(RoundedCornerShape(8.dp))
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
              modifier = Modifier.size(metrics.cardBadgeIconSize)
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Pill tag
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(GoldTagBg)
              .padding(horizontal = metrics.cardTagPadH, vertical = metrics.cardTagPadV)
          ) {
            Text(
              text = stringResource(R.string.owner_portal_tag),
              color = GoldPrimary,
              fontSize = metrics.cardTagFontSize,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.4.sp,
              fontFamily = MontserratFamily
            )
          }
        }

        Spacer(modifier = Modifier.height(metrics.cardTitleTopSpace))

        // "SALON / PARLOUR OWNER" Title
        Text(
          text = stringResource(R.string.owner_portal_title),
          color = TextWhite,
          fontSize = metrics.ownerTitleSize,
          fontWeight = FontWeight.Bold,
          fontFamily = MontserratFamily
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Subtitle
        Text(
          text = stringResource(R.string.owner_portal_desc),
          color = Color(0xFFDFD7CA),
          fontSize = metrics.cardDescFontSize,
          lineHeight = metrics.cardDescLineHeight,
          fontFamily = MontserratFamily
        )

        Spacer(modifier = Modifier.height(metrics.cardButtonTopSpace))

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
            .padding(horizontal = metrics.cardButtonPadH, vertical = metrics.cardButtonPadV)
            .testTag("continue_as_owner_button"),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = stringResource(R.string.btn_continue_owner),
              color = Color(0xFF1D1303),
              fontSize = metrics.ownerButtonFontSize,
              fontWeight = FontWeight.ExtraBold,
              fontFamily = MontserratFamily
            )
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = stringResource(R.string.content_desc_arrow),
              tint = Color(0xFF1D1303),
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      // Right Photo: Professional salon workers in uniforms
      Box(
        modifier = Modifier
          .weight(0.95f)
          .height(metrics.cardPhotoHeight),
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
  metrics: WelcomeLayoutMetrics,
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
      metrics = metrics,
      testTag = "footer_about_salonmate",
      onClick = onAboutClick
    )

    Text(
      text = "|",
      color = Color(0xFF4A4252),
      fontSize = metrics.footerFontSize,
      modifier = Modifier.padding(horizontal = 6.dp)
    )

    FooterLinkItem(
      title = stringResource(R.string.footer_support),
      icon = Icons.Outlined.HelpOutline,
      metrics = metrics,
      testTag = "footer_help_support",
      onClick = onSupportClick
    )

    Text(
      text = "|",
      color = Color(0xFF4A4252),
      fontSize = metrics.footerFontSize,
      modifier = Modifier.padding(horizontal = 6.dp)
    )

    FooterLinkItem(
      title = stringResource(R.string.footer_privacy),
      icon = Icons.Outlined.Shield,
      metrics = metrics,
      testTag = "footer_privacy_policy",
      onClick = onPrivacyClick
    )
  }
}

@Composable
private fun FooterLinkItem(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  metrics: WelcomeLayoutMetrics,
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
      .padding(horizontal = 4.dp, vertical = 4.dp)
      .testTag(testTag)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = title,
      tint = TextSecondary,
      modifier = Modifier.size(metrics.footerIconSize)
    )
    Spacer(modifier = Modifier.width(3.dp))
    Text(
      text = title,
      color = TextSecondary,
      fontSize = metrics.footerFontSize,
      fontWeight = FontWeight.Medium,
      fontFamily = MontserratFamily
    )
  }
}
