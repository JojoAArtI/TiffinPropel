package com.propel.tiffin.ui.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.propel.tiffin.TiffinApplication
import com.propel.tiffin.data.model.Kitchen
import com.propel.tiffin.ui.components.DietBadge
import com.propel.tiffin.ui.components.GhostButton
import com.propel.tiffin.ui.components.MarqueeBanner
import com.propel.tiffin.ui.components.PricePill
import com.propel.tiffin.ui.components.RatingCoin
import com.propel.tiffin.ui.components.StickerButton
import com.propel.tiffin.ui.theme.Carbon
import com.propel.tiffin.ui.theme.ConcreteGray
import com.propel.tiffin.ui.theme.Lavender
import com.propel.tiffin.ui.theme.PaperWhite
import com.propel.tiffin.ui.theme.SoftMist
import com.propel.tiffin.ui.theme.Spacing

@Composable
fun DetailScreen(
    kitchenId: String,
    isPaid: Boolean,
    onBack: () -> Unit,
    onSubscribe: (String) -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as TiffinApplication).container
    val viewModel: DetailViewModel = viewModel(
        factory = DetailViewModelFactory(container, kitchenId)
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWhite)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MarqueeBanner()

            when (val current = state) {
                is DetailUiState.Loading -> DetailLoading(onBack)
                is DetailUiState.NotFound -> DetailNotFound(onBack)
                is DetailUiState.Content -> DetailContent(
                    kitchen = current.kitchen,
                    isPaid = isPaid,
                    onBack = onBack,
                    onSubscribe = onSubscribe,
                    bottomPadding = navBarPadding.calculateBottomPadding()
                )
            }
        }
    }
}

@Composable
private fun DetailLoading(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.lg)
    ) {
        GhostButton(text = "← Back", onClick = onBack)
        Spacer(modifier = Modifier.height(Spacing.lg))
        val shape = RoundedCornerShape(20.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(shape)
                .background(SoftMist)
                .border(1.dp, Carbon, shape)
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ConcreteGray)
        )
    }
}

@Composable
private fun DetailNotFound(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Kitchen not found",
            style = MaterialTheme.typography.headlineSmall,
            color = Carbon
        )
        Spacer(modifier = Modifier.height(Spacing.md))
        GhostButton(text = "← Back", onClick = onBack)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailContent(
    kitchen: Kitchen,
    isPaid: Boolean,
    onBack: () -> Unit,
    onSubscribe: (String) -> Unit,
    bottomPadding: androidx.compose.ui.unit.Dp
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp + bottomPadding)
        ) {
            // Back button
            Box(modifier = Modifier.padding(start = Spacing.sm, top = Spacing.xs)) {
                GhostButton(text = "← Back", onClick = onBack)
            }

            // Header band
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.xxs)
                    .background(Lavender)
                    .padding(Spacing.lg)
            ) {
                Text(
                    text = kitchen.name.uppercase(),
                    style = MaterialTheme.typography.displaySmall,
                    color = Carbon
                )
                Spacer(modifier = Modifier.height(Spacing.xxs))
                Text(
                    text = kitchen.cuisine,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Carbon
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DietBadge(veg = kitchen.veg)
                    RatingCoin(rating = kitchen.rating)
                    PricePill(price = kitchen.pricePerTiffin)
                }
            }

            // Weekly menu
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md)
            ) {
                Text(
                    text = "WEEKLY MENU",
                    style = MaterialTheme.typography.titleMedium,
                    color = Carbon
                )
                Spacer(modifier = Modifier.height(Spacing.xs))

                kitchen.weeklyMenu.forEach { item ->
                    MenuRow(day = item.day, dish = item.dish)
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                }
            }
        }

        // Sticky subscribe button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(PaperWhite)
                .padding(
                    horizontal = Spacing.lg,
                    vertical = Spacing.xs
                )
                .padding(bottom = bottomPadding)
        ) {
            if (isPaid) {
                GhostButton(
                    text = "Subscribed ✓",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                val haptic = LocalHapticFeedback.current
                StickerButton(
                    text = "Subscribe",
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onSubscribe(kitchen.id)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun MenuRow(day: String, dish: String) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(BorderStroke(1.dp, Carbon), shape)
            .background(PaperWhite)
            .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val dayShape = RoundedCornerShape(12.dp)
        Box(
            modifier = Modifier
                .clip(dayShape)
                .background(Carbon)
                .border(BorderStroke(1.dp, Carbon), dayShape)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = day.uppercase().take(3),
                style = MaterialTheme.typography.labelSmall,
                color = PaperWhite
            )
        }
        Spacer(modifier = Modifier.width(Spacing.xs))
        Text(
            text = dish,
            style = MaterialTheme.typography.bodyLarge,
            color = Carbon,
            modifier = Modifier.weight(1f)
        )
    }
}
