package com.propel.tiffin.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

private val STICKER_GLYPHS = listOf("🪙", "🚀", "✅", "👛", "⭐", "🍱", "🪙", "🚀", "✅", "⭐")

@Composable
fun StickerBurst(modifier: Modifier = Modifier) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(durationMillis = 800))
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        STICKER_GLYPHS.forEachIndexed { index, glyph ->
            val angle = (index * 360.0 / STICKER_GLYPHS.size) * (Math.PI / 180.0)
            val distance = 120 + (index % 3) * 40
            val p = progress.value

            val offsetX = (cos(angle) * distance * p).toFloat()
            val offsetY = (sin(angle) * distance * p).toFloat()
            val scale = if (p < 0.5f) 0.5f + p else 1f
            val alpha = if (p > 0.6f) 1f - ((p - 0.6f) / 0.4f) else 1f

            Text(
                text = glyph,
                fontSize = 28.sp,
                modifier = Modifier
                    .offset(x = offsetX.dp, y = offsetY.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .alpha(alpha.coerceIn(0f, 1f))
            )
        }
    }
}
