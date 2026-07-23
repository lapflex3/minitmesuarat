package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = OceanPrimary,
    onPrimary = OceanOnPrimary,
    primaryContainer = OceanContainer,
    onPrimaryContainer = OceanOnContainer,
    secondary = OceanSecondary,
    onSecondary = OceanOnSecondary,
    secondaryContainer = OceanSecondaryContainer,
    onSecondaryContainer = OceanOnSecondaryContainer,
    tertiary = HighDensityAura,
    tertiaryContainer = HighDensityAuraContainer,
    onTertiaryContainer = HighDensityOnAuraContainer,
    background = HighDensityBackground,
    onBackground = Color(0xFF1A1C1E),
    surface = HighDensitySurface,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = HighDensitySurfaceVariant,
    onSurfaceVariant = Color(0xFF44474E),
    outline = HighDensityOutline,
    error = RoseError
)

private val DarkColorScheme = darkColorScheme(
    primary = HighDensityDarkPrimary,
    onPrimary = Color(0xFF003253),
    primaryContainer = Color(0xFF004A77),
    onPrimaryContainer = Color(0xFFD3E4FF),
    secondary = Color(0xFF9ECEFF),
    onSecondary = Color(0xFF003258),
    background = HighDensityDarkBg,
    onBackground = Color(0xFFE1E2E5),
    surface = HighDensityDarkSurface,
    onSurface = Color(0xFFE1E2E5),
    surfaceVariant = HighDensityDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC4C6D0),
    error = Color(0xFFFFB4AB)
)

@Composable
fun MinitAuraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic color to maintain consistent corporate identity
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
