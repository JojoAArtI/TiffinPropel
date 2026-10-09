package com.propel.tiffin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    background = Surface,
    surface = Surface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    primary = SwiggyOrange,
    onPrimary = Surface,
    outline = Divider
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
