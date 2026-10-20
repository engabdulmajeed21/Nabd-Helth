package com.example.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

val LocalAppStrings = staticCompositionLocalOf { ArabicStrings }
val LocalLanguage = staticCompositionLocalOf { "ar" }

@Composable
fun ProvideAppLocalization(
    language: String,
    content: @Composable () -> Unit
) {
    val strings = if (language == "en") EnglishStrings else ArabicStrings
    val layoutDirection = if (language == "en") LayoutDirection.Ltr else LayoutDirection.Rtl

    CompositionLocalProvider(
        LocalAppStrings provides strings,
        LocalLanguage provides language,
        LocalLayoutDirection provides layoutDirection
    ) {
        content()
    }
}
