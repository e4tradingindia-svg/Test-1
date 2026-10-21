package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SalonMateDarkColorScheme = darkColorScheme(
  primary = PinkPrimary,
  onPrimary = TextWhite,
  primaryContainer = PinkDeep,
  onPrimaryContainer = TextWhite,
  secondary = GoldPrimary,
  onSecondary = LuxuryBlack,
  secondaryContainer = GoldTagBg,
  onSecondaryContainer = GoldGradientStart,
  background = LuxuryBlack,
  onBackground = TextWhite,
  surface = LuxurySurface,
  onSurface = TextWhite,
  surfaceVariant = LuxuryBlackElevated,
  onSurfaceVariant = TextSecondary,
  outline = DarkBorder
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = SalonMateDarkColorScheme,
    typography = Typography,
    content = content
  )
}

