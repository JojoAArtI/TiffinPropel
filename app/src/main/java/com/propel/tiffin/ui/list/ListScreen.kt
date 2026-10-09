package com.propel.tiffin.ui.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.propel.tiffin.TiffinApplication
import com.propel.tiffin.ui.components.GhostButton
import com.propel.tiffin.ui.components.MarqueeBanner
import com.propel.tiffin.ui.theme.Carbon
import com.propel.tiffin.ui.theme.ConcreteGray
import com.propel.tiffin.ui.theme.PaperWhite
import com.propel.tiffin.ui.theme.SkyWash
import com.propel.tiffin.ui.theme.SoftMist
import com.propel.tiffin.ui.theme.Spacing

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListScreen(onKitchenClick: (String) -> Unit) {
    val context = LocalContext.current
    val container = (context.applicationContext as TiffinApplication).container
    val viewModel: ListViewModel = viewModel(factory = ListViewModelFactory(container))
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWhite)
    ) {
        MarqueeBanner()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SkyWash)
                .combinedClickable(
                    onClick = {},
                    onLongClick = { viewModel.simulateError() }
                )
                .padding(Spacing.lg)
        ) {
            Text(
                text = "KITCHENS",
                style = MaterialTheme.typography.displayMedium,
                color = Carbon
            )
            Text(
                text = "home kitchens near you",
                style = MaterialTheme.typography.bodyLarge,
                color = Carbon
            )
        }

        when (val current = state) {
            is ListUiState.Loading -> LoadingContent()
            is ListUiState.Empty -> EmptyContent()
            is ListUiState.Error -> ErrorContent(
                message = current.message,
                onRetry = viewModel::retry
            )
            is ListUiState.Content -> ContentList(
                kitchens = current.kitchens,
                isRefreshing = isRefreshing,
                onRefresh = viewModel::refresh,
                onKitchenClick = onKitchenClick
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(4) {
            SkeletonCard()
        }
    }
}

@Composable
private fun SkeletonCard() {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SoftMist)
            .border(1.dp, Carbon, shape)
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ConcreteGray)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.35f)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ConcreteGray)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ConcreteGray)
        )
    }
}

@Composable
private fun EmptyContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🍱", style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(Spacing.sm))
        Text(
            text = "No kitchens nearby",
            style = MaterialTheme.typography.headlineSmall,
            color = Carbon
        )
        Spacer(modifier = Modifier.height(Spacing.xxs))
        Text(
            text = "We're expanding to your area soon",
            style = MaterialTheme.typography.bodyLarge,
            color = Carbon
        )
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Couldn't load kitchens",
            style = MaterialTheme.typography.headlineSmall,
            color = Carbon
        )
        Spacer(modifier = Modifier.height(Spacing.xxs))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Carbon
        )
        Spacer(modifier = Modifier.height(Spacing.md))
        GhostButton(text = "Retry", onClick = onRetry)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentList(
    kitchens: List<com.propel.tiffin.data.model.Kitchen>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onKitchenClick: (String) -> Unit
) {
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh
    ) {
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = 16.dp + navBarPadding.calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(kitchens, key = { it.id }) { kitchen ->
                KitchenCard(
                    kitchen = kitchen,
                    onClick = { onKitchenClick(kitchen.id) }
                )
            }
        }
    }
}
