package com.example.ui.welcome

import androidx.annotation.DrawableRes
import com.example.R

/**
 * Pluggable asset registry for SalonMate branding and character visuals.
 * Allows effortless replacement with high-resolution photographic PNGs/WebPs
 * or remote URLs for multi-platform (Android, Web, iOS) synchronization.
 */
object SalonMateAssets {
  @DrawableRes val scissorsIcon: Int = R.drawable.ic_scissors
  @DrawableRes val facialIcon: Int = R.drawable.ic_facial_care
  @DrawableRes val makeupIcon: Int = R.drawable.ic_makeup_beauty
  @DrawableRes val nailIcon: Int = R.drawable.ic_nail_care
  @DrawableRes val spaIcon: Int = R.drawable.ic_spa_lotus
  @DrawableRes val beardIcon: Int = R.drawable.ic_beard_grooming
  @DrawableRes val moreIcon: Int = R.drawable.ic_more_services
  
  @DrawableRes val userSearchIcon: Int = R.drawable.ic_user_search
  @DrawableRes val storefrontIcon: Int = R.drawable.ic_storefront
  @DrawableRes val shieldCheckIcon: Int = R.drawable.ic_shield_check
  @DrawableRes val calendarBookingIcon: Int = R.drawable.ic_calendar_booking
  @DrawableRes val trustedStarIcon: Int = R.drawable.ic_trusted_professionals

  // Realistic photographic salon imagery and original logo
  @DrawableRes val logo: Int = R.drawable.salonmate_original_logo
  @DrawableRes val menHero: Int = R.drawable.salonmate_men_hero
  @DrawableRes val womenHero: Int = R.drawable.salonmate_women_hero
  @DrawableRes val heroBanner: Int = R.drawable.hero_salon_banner
  @DrawableRes val customerPhoto: Int = R.drawable.customer_card_photo
  @DrawableRes val ownerPhoto: Int = R.drawable.owner_card_photo
}
