package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MakaoLightColorScheme = lightColorScheme(
    primary = AccentCyan,
    onPrimary = BgCard,
    secondary = AccentGold,
    onSecondary = TextPrimary,
    tertiary = AccentGreen,
    background = BgDeep,
    onBackground = TextPrimary,
    surface = BgCard,
    onSurface = TextPrimary,
    surfaceVariant = AccentOrange,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder
)

@Composable
fun MakaoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MakaoLightColorScheme,
        typography = Typography,
        content = content
    )
}

