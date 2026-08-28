package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.model.AppTheme
import com.example.model.CalculatorState
import com.example.ui.screens.CalculatorScreen
import com.example.ui.theme.CalculatorTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      CalculatorTheme(appTheme = AppTheme.DARK) {
        CalculatorScreen(
          state = CalculatorState(
            primaryDisplay = "125,000",
            secondaryDisplay = "125 × 1,000 ="
          ),
          currentTheme = AppTheme.DARK,
          onKeyClick = {},
          onOpenHistory = {},
          onOpenSettings = {},
          onToggleTheme = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

