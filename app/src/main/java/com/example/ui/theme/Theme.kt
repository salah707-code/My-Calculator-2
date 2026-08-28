package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.model.AccentColor
import com.example.model.AppTheme

fun createDarkColorScheme(accentColor: AccentColor) = darkColorScheme(
    primary = accentColor.darkColor,
    onPrimary = Color.White,
    primaryContainer = accentColor.darkSoft,
    onPrimaryContainer = accentColor.darkColor,
    secondary = DarkUtilityKeyBg,
    onSecondary = DarkUtilityKeyText,
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkCardBorder,
    error = CleanRedDark
)

fun createLightColorScheme(accentColor: AccentColor) = lightColorScheme(
    primary = accentColor.color,
    onPrimary = Color.White,
    primaryContainer = accentColor.lightSoft,
    onPrimaryContainer = accentColor.color,
    secondary = LightUtilityKeyBg,
    onSecondary = LightUtilityKeyText,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightCardBorder,
    error = CleanRed
)

fun createMetallicColorScheme(accentColor: AccentColor) = darkColorScheme(
    primary = accentColor.darkColor,
    onPrimary = Color.White,
    primaryContainer = MetallicOperatorKeyBg,
    onPrimaryContainer = MetallicOperatorKeyText,
    secondary = MetallicUtilityKeyBg,
    onSecondary = MetallicUtilityKeyText,
    background = MetallicBg,
    onBackground = MetallicTextPrimary,
    surface = MetallicSurface,
    onSurface = MetallicTextPrimary,
    surfaceVariant = MetallicSurfaceVariant,
    onSurfaceVariant = MetallicTextSecondary,
    outline = MetallicCardBorder,
    error = CleanRedDark
)

fun createAmoledColorScheme(accentColor: AccentColor) = darkColorScheme(
    primary = accentColor.darkColor,
    onPrimary = Color.White,
    primaryContainer = AmoledOperatorKeyBg,
    onPrimaryContainer = accentColor.darkColor,
    secondary = AmoledUtilityKeyBg,
    onSecondary = AmoledUtilityKeyText,
    background = AmoledBg,
    onBackground = AmoledTextPrimary,
    surface = AmoledSurface,
    onSurface = AmoledTextPrimary,
    surfaceVariant = AmoledSurfaceVariant,
    onSurfaceVariant = AmoledTextSecondary,
    outline = AmoledCardBorder,
    error = CleanRedDark
)

fun createMidnightColorScheme(accentColor: AccentColor) = darkColorScheme(
    primary = accentColor.darkColor,
    onPrimary = Color.White,
    primaryContainer = MidnightOperatorKeyBg,
    onPrimaryContainer = accentColor.darkColor,
    secondary = MidnightUtilityKeyBg,
    onSecondary = MidnightUtilityKeyText,
    background = MidnightBg,
    onBackground = MidnightTextPrimary,
    surface = MidnightSurface,
    onSurface = MidnightTextPrimary,
    surfaceVariant = MidnightSurfaceVariant,
    onSurfaceVariant = MidnightTextSecondary,
    outline = MidnightCardBorder,
    error = CleanRedDark
)

@Composable
fun CalculatorTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    accentColor: AccentColor = AccentColor.BLUE,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (appTheme) {
        AppTheme.SYSTEM -> isSystemDark
        AppTheme.LIGHT -> false
        AppTheme.DARK, AppTheme.METALLIC, AppTheme.AMOLED, AppTheme.MIDNIGHT_BLUE -> true
    }

    val colorScheme: ColorScheme = when (appTheme) {
        AppTheme.SYSTEM -> if (isSystemDark) createDarkColorScheme(accentColor) else createLightColorScheme(accentColor)
        AppTheme.LIGHT -> createLightColorScheme(accentColor)
        AppTheme.DARK -> createDarkColorScheme(accentColor)
        AppTheme.METALLIC -> createMetallicColorScheme(accentColor)
        AppTheme.AMOLED -> createAmoledColorScheme(accentColor)
        AppTheme.MIDNIGHT_BLUE -> createMidnightColorScheme(accentColor)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.surface.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !isDark
                insetsController.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

