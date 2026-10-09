package com.propel.tiffin.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.propel.tiffin.ui.theme.Carbon
import com.propel.tiffin.ui.theme.PaperWhite

private const val MARQUEE_TEXT = "TIFFIN • FRESH FROM HOME KITCHENS • ₹1 TRIAL TODAY • "

@Composable
fun MarqueeBanner(modifier: Modifier = Modifier) {
    val repeatedText = MARQUEE_TEXT.repeat(10)
    val textWidthPx = remember { mutableIntStateOf(0) }
    val halfWidth = textWidthPx.intValue / 2

    val infiniteTransition = rememberInfiniteTransition(label = "marquee")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (halfWidth > 0) -halfWidth.toFloat() else -1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (halfWidth > 0) (halfWidth * 20 / 10) else 8000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "marqueeOffset"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Carbon)
            .clipToBounds()
    ) {
        Text(
            text = repeatedText,
            style = MaterialTheme.typography.labelSmall,
            color = PaperWhite,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            softWrap = false,
            modifier = Modifier
                .statusBarsPadding()
                .padding(vertical = 8.dp)
                .onSizeChanged { textWidthPx.intValue = it.width }
                .offset { IntOffset(offset.toInt(), 0) }
        )
    }
}
