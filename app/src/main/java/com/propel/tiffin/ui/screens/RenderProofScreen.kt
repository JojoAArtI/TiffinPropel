package com.propel.tiffin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.propel.tiffin.ui.components.GhostButton
import com.propel.tiffin.ui.components.StickerButton
import com.propel.tiffin.ui.theme.Carbon
import com.propel.tiffin.ui.theme.PaperWhite
import com.propel.tiffin.ui.theme.SkyWash
import com.propel.tiffin.ui.theme.Spacing
import com.propel.tiffin.ui.theme.TiffinTheme

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RenderProofScreenPreview() {
    TiffinTheme {
        RenderProofScreen()
    }
}

@Composable
fun RenderProofScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWhite)
    ) {
        MarqueeBanner()

        HeroSection()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            horizontalAlignment = Alignment.Start
        ) {
            StickerButton(text = "Get Started", onClick = {})
            GhostButton(text = "Browse", onClick = {})
        }
    }
}

@Composable
private fun MarqueeBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Carbon)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "TIFFIN • FRESH FROM HOME KITCHENS • ₹1 TRIAL TODAY • "
                .repeat(5),
            style = MaterialTheme.typography.labelSmall,
            color = PaperWhite,
            maxLines = 1,
            overflow = TextOverflow.Clip
        )
    }
}

@Composable
private fun HeroSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SkyWash)
            .padding(Spacing.lg)
    ) {
        Text(
            text = "TIFFIN",
            style = MaterialTheme.typography.displayLarge,
            color = Carbon
        )
        Text(
            text = "home kitchens near you",
            style = MaterialTheme.typography.bodyLarge,
            color = Carbon
        )
    }
}
