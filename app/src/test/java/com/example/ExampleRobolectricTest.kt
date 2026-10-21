package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SalonMate", appName)
  }

  @Test
  fun `verify welcome screen strings exist`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    assertEquals("BOOK • BEAUTY • BE YOU", context.getString(R.string.tagline))
    assertEquals("CUSTOMER", context.getString(R.string.customer_portal_title))
    assertEquals("SALON / PARLOUR OWNER", context.getString(R.string.owner_portal_title))
  }
}
