package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PinkPrimary
import com.example.ui.welcome.SalonMateAssets

/**
 * SalonMate Emblem Badge:
 * Displays the original SalonMate logo asset directly.
 */
@Composable
fun SalonMateLogoBadge(
  modifier: Modifier = Modifier,
  badgeWidth: Dp = 142.dp,
  badgeHeight: Dp = 142.dp
) {
  val badgeShape = RoundedCornerShape(28.dp)

  Box(
    modifier = modifier
      .width(badgeWidth)
      .height(badgeHeight)
      .shadow(
        elevation = 20.dp,
        shape = badgeShape,
        ambientColor = PinkPrimary.copy(alpha = 0.5f),
        spotColor = GoldPrimary.copy(alpha = 0.6f)
      )
      .clip(badgeShape),
    contentAlignment = Alignment.Center
  ) {
    Image(
      painter = painterResource(id = SalonMateAssets.logo),
      contentDescription = stringResource(R.string.content_desc_logo),
      contentScale = ContentScale.Fit,
      modifier = Modifier.fillMaxSize()
    )
  }
}
