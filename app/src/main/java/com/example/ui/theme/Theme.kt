package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.localization.ProvideAppLocalization

private val DarkColorScheme = darkColorScheme(
    primary = MedicalBlueSecondary,
    onPrimary = Color.White,
    primaryContainer = MedicalBlueContainerDark,
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = MedicalBlueSecondary,
    onSecondary = Color.White,
    tertiary = SuccessGreen,
    onTertiary = Color.White,
    background = BgDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    error = DangerRed,
    onError = Color.White,
    outline = BorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = MedicalBluePrimary,
    onPrimary = Color.White,
    primaryContainer = MedicalBlueContainerLight,
    onPrimaryContainer = MedicalBlueDark,
    secondary = MedicalBlueSecondary,
    onSecondary = Color.White,
    tertiary = SuccessGreen,
    onTertiary = Color.White,
    background = BgLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    error = DangerRed,
    onError = Color.White,
    outline = BorderLight
)

@Composable
fun AuratechTheme(
    themeMode: String = "system",
    language: String = "ar",
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val typography = getAppTypography(language)

    ProvideAppLocalization(language = language) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = AppShapes,
            content = content
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AuratechTheme(
        themeMode = if (darkTheme) "dark" else "light",
        language = "en",
        content = content
    )
}

