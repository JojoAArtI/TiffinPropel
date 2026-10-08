package com.propel.tiffin.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.propel.tiffin.ui.theme.Carbon
import com.propel.tiffin.ui.theme.Sunburst

@Composable
fun RatingCoin(rating: Double, modifier: Modifier = Modifier) {
    val display = String.format("%.1f", rating)

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Sunburst)
            .border(BorderStroke(1.dp, Carbon), CircleShape)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .semantics { contentDescription = "Rating $display out of 5" }
    ) {
        Text(
            text = "★ $display",
            style = MaterialTheme.typography.labelLarge,
            color = Carbon
        )
    }
}
