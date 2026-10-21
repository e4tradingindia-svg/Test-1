package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val MontserratFamily = FontFamily(
  Font(R.font.montserrat, FontWeight.Normal)
)

val CinzelFamily = FontFamily(
  Font(R.font.cinzel, FontWeight.Normal)
)

val Typography = Typography(
  displayLarge = TextStyle(
    fontFamily = MontserratFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 34.sp,
    letterSpacing = 0.5.sp
  ),
  displayMedium = TextStyle(
    fontFamily = MontserratFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    letterSpacing = 0.5.sp
  ),
  headlineLarge = TextStyle(
    fontFamily = MontserratFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 22.sp,
    letterSpacing = 0.5.sp
  ),
  headlineMedium = TextStyle(
    fontFamily = MontserratFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    letterSpacing = 0.3.sp
  ),
  titleMedium = TextStyle(
    fontFamily = MontserratFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 15.sp,
    letterSpacing = 0.2.sp
  ),
  bodyLarge = TextStyle(
    fontFamily = MontserratFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.25.sp
  ),
  bodyMedium = TextStyle(
    fontFamily = MontserratFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.2.sp
  ),
  labelLarge = TextStyle(
    fontFamily = MontserratFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
    letterSpacing = 0.5.sp
  ),
  labelSmall = TextStyle(
    fontFamily = MontserratFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 10.sp,
    letterSpacing = 0.5.sp
  )
)

