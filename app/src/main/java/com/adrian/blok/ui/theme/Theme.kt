package com.adrian.blok.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Green = Color(0xFF1F6B4A)
private val Ink = Color(0xFF14201A)
private val Soft = Color(0xFFEEF3EF)
private val Danger = Color(0xFF9B2C2C)

private val ColorScheme = lightColorScheme(
    primary = Green,
    onPrimary = Color(0xFFF4FFF8),
    background = Soft,
    onBackground = Ink,
    surface = Color(0xFFFFFCF7),
    onSurface = Ink,
    error = Danger,
    onError = Color.White,
)

@Composable
fun BlokTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        content = content,
    )
}
