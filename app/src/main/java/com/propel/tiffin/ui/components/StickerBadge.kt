package com.propel.tiffin.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.propel.tiffin.ui.theme.Carbon
import com.propel.tiffin.ui.theme.Ember
import com.propel.tiffin.ui.theme.MintPop
import com.propel.tiffin.ui.theme.PaperWhite

@Composable
fun DietBadge(veg: Boolean, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(16.dp)
    val fillColor = if (veg) MintPop else Ember
    val textColor = if (veg) Carbon else PaperWhite
    val label = if (veg) "VEG" else "NON-VEG"
    val description = if (veg) "Vegetarian" else "Non-vegetarian"

    Box(
        modifier = modifier
            .clip(shape)
            .background(fillColor)
            .border(BorderStroke(1.dp, Carbon), shape)
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .semantics { contentDescription = description }
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}
