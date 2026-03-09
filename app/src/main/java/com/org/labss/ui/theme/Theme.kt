package com.org.labss.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.org.labss.ui.theme.BlackPrimary
import com.org.labss.ui.theme.LightGraySurface
import com.org.labss.ui.theme.White

private val AppColorScheme = lightColorScheme(
    background = White,
    surface = LightGraySurface,
    primary = BlackPrimary,
    onPrimary = White,
    onBackground = BlackPrimary,
    onSurface = BlackPrimary
)

@Composable
fun LabssTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}