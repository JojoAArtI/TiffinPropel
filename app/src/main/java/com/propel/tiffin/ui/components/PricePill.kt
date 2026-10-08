package com.propel.tiffin.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.propel.tiffin.ui.theme.Carbon
import com.propel.tiffin.ui.theme.Lavender
import com.propel.tiffin.ui.theme.Pill

@Composable
fun PricePill(price: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(Pill)
            .background(Lavender)
            .border(BorderStroke(1.dp, Carbon), Pill)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = "₹$price",
            style = MaterialTheme.typography.labelLarge,
            color = Carbon
        )
    }
}
