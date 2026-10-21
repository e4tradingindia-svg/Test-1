package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LuxuryBlack
import com.example.ui.theme.PinkPrimary
import com.example.ui.welcome.SalonMateAssets

/**
 * Realistic Photographic Salon Imagery Components for SalonMate:
 * - Top hero: realistic photo of male barber cutting hair on left (salonmate_men_hero),
 *   female beauty professional giving a cucumber facial on right (salonmate_women_hero).
 * - Customer card: realistic photo of male customer in barber cape and female facial customer.
 * - Owner card: realistic photo of professional male and female salon team in uniforms.
 */

@Composable
fun HeroSalonPhotoBanner(
  modifier: Modifier = Modifier,
  height: Dp = 240.dp
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(height)
      .background(LuxuryBlack)
  ) {
    // Top Hero Dual Photos: Left = Men Salon Barber, Right = Women Beauty Parlour
    Row(
      modifier = Modifier.fillMaxSize()
    ) {
      // Left: Male barber cutting male customer's hair
      Image(
        painter = painterResource(id = R.drawable.salonmate_men_hero),
        contentDescription = "Men Salon Barber - Haircut & Styling",
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight()
      )

      // Right: Female beauty professional giving cucumber facial to female customer
      Image(
        painter = painterResource(id = R.drawable.salonmate_women_hero),
        contentDescription = "Women Beauty Parlour - Facial & Skincare",
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight()
      )
    }

    // Center subtle dark vignette where the original logo floats
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          brush = Brush.horizontalGradient(
            colors = listOf(
              Color.Transparent,
              LuxuryBlack.copy(alpha = 0.4f),
              LuxuryBlack.copy(alpha = 0.85f),
              LuxuryBlack.copy(alpha = 0.4f),
              Color.Transparent
            )
          )
        )
    )

    // Gradient fade at bottom to blend into LuxuryBlack (#08070B)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(70.dp)
        .align(Alignment.BottomCenter)
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(
              Color.Transparent,
              LuxuryBlack.copy(alpha = 0.85f),
              LuxuryBlack
            )
          )
        )
    )
  }
}

@Composable
fun CustomerCardPhoto(
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(topEnd = 18.dp, bottomEnd = 18.dp)

  Box(
    modifier = modifier
      .clip(shape)
  ) {
    Image(
      painter = painterResource(id = SalonMateAssets.customerPhoto),
      contentDescription = "SalonMate Customers - Haircut Client and Spa Facial Client",
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxSize()
    )

    // Subtle dark gradient on left edge to blend seamlessly with customer card text
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          brush = Brush.horizontalGradient(
            colors = listOf(
              Color(0xFF160B1E),
              Color(0x99160B1E),
              Color.Transparent
            ),
            startX = 0f,
            endX = 140f
          )
        )
    )
  }
}

@Composable
fun OwnerCardPhoto(
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(topEnd = 18.dp, bottomEnd = 18.dp)

  Box(
    modifier = modifier
      .clip(shape)
  ) {
    Image(
      painter = painterResource(id = SalonMateAssets.ownerPhoto),
      contentDescription = "SalonMate Professionals - Stylists and Beauty Experts in Uniform",
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxSize()
    )

    // Subtle dark gradient on left edge to blend seamlessly with business card text
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          brush = Brush.horizontalGradient(
            colors = listOf(
              Color(0xFF19120A),
              Color(0x9919120A),
              Color.Transparent
            ),
            startX = 0f,
            endX = 140f
          )
        )
    )
  }
}
