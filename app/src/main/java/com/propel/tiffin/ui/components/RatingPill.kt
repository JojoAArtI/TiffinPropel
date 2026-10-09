package com.propel.tiffin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.propel.tiffin.ui.theme.Surface
import com.propel.tiffin.ui.theme.RatingGreenDk

@Composable
fun RatingPill(rating: Double, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(6.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(RatingGreenDk)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "★ ${String.format("%.1f", rating)}",
            style = MaterialTheme.typography.labelMedium,
            color = Surface
        )
    }
}
