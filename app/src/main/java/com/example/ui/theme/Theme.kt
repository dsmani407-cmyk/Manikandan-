package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.model.ThemeMode

val LocalThemeMode = staticCompositionLocalOf { ThemeMode.LIGHT }
val LocalIsFieldSalesDark = staticCompositionLocalOf { false }

// High-Contrast Dark Mode specifically engineered for maximum outdoor & in-vehicle readability during field sales calls
private val FieldSalesDarkHighContrastColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8), // Electric Indigo for high visibility against deep background
    onPrimary = Color(0xFF030712),
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = Color(0xFF34D399), // Luminous Emerald for call & closure actions
    onSecondary = Color(0xFF022C22),
    secondaryContainer = Color(0xFF065F46),
    onSecondaryContainer = Color(0xFFD1FAE5),
    tertiary = Color(0xFFFBBF24), // Vibrant Amber for follow-up reminders
    onTertiary = Color(0xFF451A03),
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = Color(0xFFFEF3C7),
    background = Color(0xFF070B14), // Deep pitch obsidian canvas — eliminates glare and reflections in bright sunlight
    onBackground = Color(0xFFFFFFFF), // Pure brilliant white text for effortless outdoor reading
    surface = Color(0xFF0F172A), // Elevated high-contrast card surface
    onSurface = Color(0xFFFFFFFF), // Pure white primary text on cards
    surfaceVariant = Color(0xFF1E293B), // High-contrast inner pill/badge background
    onSurfaceVariant = Color(0xFFE2E8F0), // Bright silver text for secondary labels
    outline = Color(0xFF475569), // Crisp border definition between cards in daylight
    outlineVariant = Color(0xFF334155)
)

// Professional Light Mode for office environments, presentations and desktop/admin reviews
private val ProfessionalLightColorScheme = lightColorScheme(
    primary = Indigo600,
    onPrimary = Color.White,
    primaryContainer = Indigo50,
    onPrimaryContainer = Indigo700,
    secondary = Emerald600,
    onSecondary = Color.White,
    secondaryContainer = Emerald50,
    onSecondaryContainer = Emerald800,
    tertiary = Amber600,
    onTertiary = Color.White,
    tertiaryContainer = Amber50,
    onTertiaryContainer = Amber900,
    background = Slate50,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate200,
    outlineVariant = Slate100
)

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.LIGHT,
    content: @Composable () -> Unit
) {
    val isDark = themeMode.isDark
    val colorScheme = if (isDark) FieldSalesDarkHighContrastColorScheme else ProfessionalLightColorScheme

    CompositionLocalProvider(
        LocalThemeMode provides themeMode,
        LocalIsFieldSalesDark provides isDark
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

