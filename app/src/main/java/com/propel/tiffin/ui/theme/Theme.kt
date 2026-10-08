package com.propel.tiffin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    background = PaperWhite,
    surface = PaperWhite,
    onBackground = Carbon,
    onSurface = Carbon,
    primary = Carbon,
    onPrimary = PaperWhite,
    outline = Carbon
)

@Composable
fun TiffinTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = TiffinTypography,
        shapes = TiffinShapes,
        content = content
    )
}
