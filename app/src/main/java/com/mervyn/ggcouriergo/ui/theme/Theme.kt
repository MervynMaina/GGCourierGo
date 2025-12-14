package com.mervyn.ggcouriergo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ---------------------- 1. Custom Color Definitions ----------------------

// Centralized color definitions for easy use across the app
object GGColors {
    // Primary Brand Colors
    val GreenPrimary = Color(0xFF1B8F3A) // Used for main buttons, app bar, dominant elements
    val GreenSecondary = Color(0xFF37C15A) // Used for accents, secondary buttons, progress indicators

    // UI/Neutral Colors
    val GrayBackground = Color(0xFFF5F5F5) // Main screen background
    val GraySurface = Color(0xFFEDEDED) // Card/container background

    // Other useful colors (You can expand this later)
    val ErrorRed = Color(0xFFB00020)
    val SuccessGreen = Color(0xFF4CAF50)
    val White = Color.White
    val Black = Color.Black
}

// ---------------------- 2. Color Schemes ----------------------

private val LightColorScheme = lightColorScheme(
    primary = GGColors.GreenPrimary,
    onPrimary = GGColors.White,

    secondary = GGColors.GreenSecondary,
    onSecondary = GGColors.White,

    // Default background colors
    background = GGColors.GrayBackground,
    onBackground = GGColors.Black,

    // Default surface colors (for Cards, Sheets, etc.)
    surface = GGColors.GraySurface,
    onSurface = GGColors.Black,

    // Error Handling
    error = GGColors.ErrorRed,
    onError = GGColors.White,
)

private val DarkColorScheme = darkColorScheme(
    // NOTE: You will need to define suitable dark mode colors here.
    // For now, we will use a temporary dark scheme, but we will focus on the Light one.
    primary = Color(0xFF6CBE7E), // Lighter green for contrast on dark
    onPrimary = Color.Black,
    secondary = Color(0xFF90EE90),
    background = Color(0xFF121212), // Dark gray background
    surface = Color(0xFF1E1E1E), // Darker surface
    onBackground = Color.White,
    onSurface = Color.White,
)

// ---------------------- 3. The Theme Composable ----------------------

@Composable
fun GGCourierGoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // We disable dynamicColor for now to ensure *perfect* uniformity across devices
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Disabling dynamic color and platform checks for uniformity goal
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Assuming Typography is defined in Typography.kt
        shapes = Shapes(), // Assuming Shapes is defined or you use default shapes
        content = content
    )
}