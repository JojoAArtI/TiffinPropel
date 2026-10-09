package com.propel.tiffin.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.propel.tiffin.ui.theme.NonVegRed
import com.propel.tiffin.ui.theme.VegGreen

@Composable
fun VegMark(modifier: Modifier = Modifier) {
    DietMark(color = VegGreen, description = "Vegetarian", modifier = modifier)
}

@Composable
fun NonVegMark(modifier: Modifier = Modifier) {
    DietMark(color = NonVegRed, description = "Non-vegetarian", modifier = modifier)
}

@Composable
private fun DietMark(color: Color, description: String, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .size(16.dp)
            .semantics { contentDescription = description }
    ) {
        val stroke = 1.5.dp.toPx()
        val radius = 2.dp.toPx()
        drawRoundRect(
            color = color,
            style = Stroke(width = stroke),
            cornerRadius = CornerRadius(radius, radius)
        )
        drawCircle(
            color = color,
            radius = size.minDimension * 0.22f,
            center = Offset(size.width / 2, size.height / 2)
        )
    }
}
