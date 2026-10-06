package com.tide.app.core.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

data class TideExtendedColors(
    val cardBackground: Color,
    val cardBorder: Color,
    val glassBackground: Color,
    val glassBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val success: Color,
    val successContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val slip: Color,
    val slipContainer: Color,
    val isDark: Boolean
)

val LocalTideColors = staticCompositionLocalOf {
    TideExtendedColors(
        cardBackground = TideLightSurface,
        cardBorder = TideLightBorder,
        glassBackground = TideGlassLight,
        glassBorder = TideGlassBorderLight,
        textPrimary = TideTextPrimaryLight,
        textSecondary = TideTextSecondaryLight,
        textTertiary = TideTextTertiaryLight,
        success = TideSuccess,
        successContainer = TideSuccessContainer,
        warning = TideWarning,
        warningContainer = TideWarningContainer,
        slip = TideSlip,
        slipContainer = TideSlipContainer,
        isDark = false
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = TidePrimaryLuminous,
    onPrimary = TideOceanDeep,
    primaryContainer = TideOceanElevated,
    onPrimaryContainer = TidePrimaryLuminous,
    secondary = TideTealLuminous,
    onSecondary = TideOceanDeep,
    background = TideOceanDeep,
    onBackground = TideTextPrimaryDark,
    surface = TideOceanSurface,
    onSurface = TideTextPrimaryDark,
    surfaceVariant = TideOceanElevated,
    onSurfaceVariant = TideTextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = TidePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = TideTeal,
    onSecondary = Color.White,
    background = TideLightBackground,
    onBackground = TideTextPrimaryLight,
    surface = TideLightSurface,
    onSurface = TideTextPrimaryLight,
    surfaceVariant = TideLightElevated,
    onSurfaceVariant = TideTextSecondaryLight
)

@Composable
fun TideTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val extendedColors = if (darkTheme) {
        TideExtendedColors(
            cardBackground = TideOceanSurface,
            cardBorder = TideOceanBorder,
            glassBackground = TideGlassDark,
            glassBorder = TideGlassBorderDark,
            textPrimary = TideTextPrimaryDark,
            textSecondary = TideTextSecondaryDark,
            textTertiary = TideTextTertiaryDark,
            success = TideSuccess,
            successContainer = TideSuccessContainer,
            warning = TideWarning,
            warningContainer = TideWarningContainer,
            slip = TideSlip,
            slipContainer = TideSlipContainer,
            isDark = true
        )
    } else {
        TideExtendedColors(
            cardBackground = TideLightSurface,
            cardBorder = TideLightBorder,
            glassBackground = TideGlassLight,
            glassBorder = TideGlassBorderLight,
            textPrimary = TideTextPrimaryLight,
            textSecondary = TideTextSecondaryLight,
            textTertiary = TideTextTertiaryLight,
            success = TideSuccess,
            successContainer = TideSuccessContainer,
            warning = TideWarning,
            warningContainer = TideWarningContainer,
            slip = TideSlip,
            slipContainer = TideSlipContainer,
            isDark = false
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalTideColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = TideTypography,
            content = content
        )
    }
}

object TideTheme {
    val colors: TideExtendedColors
        @Composable
        get() = LocalTideColors.current
}
