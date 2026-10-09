package com.propel.tiffin.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.propel.tiffin.TiffinApplication
import com.propel.tiffin.data.model.Kitchen
import com.propel.tiffin.ui.components.NonVegMark
import com.propel.tiffin.ui.components.RatingPill
import com.propel.tiffin.ui.components.VegMark
import com.propel.tiffin.ui.theme.Divider
import com.propel.tiffin.ui.theme.RatingGreenDk
import com.propel.tiffin.ui.theme.Surface
import com.propel.tiffin.ui.theme.SurfaceMuted
import com.propel.tiffin.ui.theme.SwiggyOrange
import com.propel.tiffin.ui.theme.TextPrimary
import com.propel.tiffin.ui.theme.TextSecondary
import com.propel.tiffin.ui.theme.TextTertiary

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
            .background(Surface)
    ) {
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

@Composable
private fun DetailLoading(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        BackButton(onBack)
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceMuted)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Divider)
        )
    }
}

@Composable
private fun DetailNotFound(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Kitchen not found",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))
        BackButton(onBack)
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(36.dp)
            .shadow(4.dp, CircleShape),
        colors = IconButtonDefaults.iconButtonColors(containerColor = Surface)
    ) {
        Icon(
            painter = painterResource(android.R.drawable.ic_menu_revert),
            contentDescription = "Back",
            tint = TextPrimary,
            modifier = Modifier.size(18.dp)
        )
    }
}

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
                .padding(bottom = 72.dp + bottomPadding)
        ) {
            Box {
                AsyncImage(
                    model = "file:///android_asset/${kitchen.imageAsset}",
                    contentDescription = kitchen.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent),
                                endY = 100f
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(12.dp)
                ) {
                    BackButton(onBack)
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = kitchen.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingPill(rating = kitchen.rating)
                    Text(
                        text = " • 20–25 mins",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (kitchen.veg) VegMark() else NonVegMark()
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${kitchen.cuisine} • ₹${kitchen.pricePerTiffin} for one",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Free delivery on orders above ₹49",
                    style = MaterialTheme.typography.labelMedium,
                    color = SwiggyOrange
                )
            }

            HorizontalDivider(color = Divider)

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "This week's menu (${kitchen.weeklyMenu.size} days)",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                kitchen.weeklyMenu.forEachIndexed { index, item ->
                    DishRow(
                        dishName = item.dish,
                        dayLabel = item.day,
                        veg = kitchen.veg,
                        isBestseller = index == 0
                    )
                    if (index < kitchen.weeklyMenu.lastIndex) {
                        HorizontalDivider(
                            color = Divider,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Surface)
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .padding(bottom = bottomPadding)
        ) {
            if (isPaid) {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RatingGreenDk)
                ) {
                    Text(
                        text = "SUBSCRIBED ✓",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            } else {
                val haptic = LocalHapticFeedback.current
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onSubscribe(kitchen.id)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SwiggyOrange,
                        contentColor = Surface
                    )
                ) {
                    Text(
                        text = "SUBSCRIBE",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun DishRow(
    dishName: String,
    dayLabel: String,
    veg: Boolean,
    isBestseller: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (veg) VegMark() else NonVegMark()
            if (isBestseller) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Bestseller",
                    style = MaterialTheme.typography.labelSmall,
                    color = SwiggyOrange
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = dishName,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Available $dayLabel",
            style = MaterialTheme.typography.bodyMedium,
            color = TextTertiary
        )
    }
}
