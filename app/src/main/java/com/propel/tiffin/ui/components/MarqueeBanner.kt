package com.propel.tiffin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.propel.tiffin.ui.theme.Carbon
import com.propel.tiffin.ui.theme.PaperWhite

@Composable
fun MarqueeBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Carbon),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "TIFFIN • FRESH FROM HOME KITCHENS • ₹1 TRIAL TODAY • "
                .repeat(5),
            style = MaterialTheme.typography.labelSmall,
            color = PaperWhite,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            modifier = Modifier
                .statusBarsPadding()
                .padding(vertical = 8.dp)
        )
    }
}
