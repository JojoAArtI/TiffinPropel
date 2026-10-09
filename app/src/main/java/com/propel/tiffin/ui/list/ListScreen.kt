package com.propel.tiffin.ui.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.propel.tiffin.data.model.Kitchen
import com.propel.tiffin.ui.components.CategoryChip
import com.propel.tiffin.ui.components.GhostButton
import com.propel.tiffin.ui.components.TiffinSearchBar
import com.propel.tiffin.ui.theme.Divider
import com.propel.tiffin.ui.theme.Surface
import com.propel.tiffin.ui.theme.SurfaceMuted
import com.propel.tiffin.ui.theme.SwiggyOrange
import com.propel.tiffin.ui.theme.TextPrimary
import com.propel.tiffin.ui.theme.TextSecondary
import com.propel.tiffin.ui.theme.TextTertiary

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
            .background(Surface)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {},
                    onLongClick = { viewModel.simulateError() }
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Tiffin",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Text(
                text = "Home-cooked meals near you",
                style = MaterialTheme.typography.bodyMedium,
                color = TextTertiary
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
                viewModel = viewModel,
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(4) {
            SkeletonCard()
        }
    }
}

@Composable
private fun SkeletonCard() {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SurfaceMuted)
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Divider)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Divider)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Divider)
        )
    }
}

@Composable
private fun EmptyContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No kitchens nearby",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "We're expanding to your area soon",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Couldn't load kitchens",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(20.dp))
        GhostButton(text = "Retry", onClick = onRetry)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentList(
    viewModel: ListViewModel,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onKitchenClick: (String) -> Unit
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val selectedCuisine by viewModel.selectedCuisine.collectAsStateWithLifecycle()
    val cuisines by viewModel.cuisines.collectAsStateWithLifecycle()
    val visible by viewModel.visibleKitchens.collectAsStateWithLifecycle()
    val hasFilters by viewModel.hasActiveFilters.collectAsStateWithLifecycle()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh
    ) {
        LazyColumn(
            contentPadding = PaddingValues(
                bottom = 16.dp + navBarPadding.calculateBottomPadding()
            )
        ) {
            item {
                TiffinSearchBar(
                    value = query,
                    onValueChange = viewModel::onQueryChange,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(cuisines) { (cuisine, imageAsset) ->
                        CategoryChip(
                            label = cuisine,
                            imageUrl = "file:///android_asset/$imageAsset",
                            selected = selectedCuisine == cuisine,
                            onClick = { viewModel.onCuisineSelected(cuisine) }
                        )
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Divider)
                )
            }

            if (visible.isNotEmpty()) {
                item {
                    Text(
                        text = "Top rated near you",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                    )
                }

                items(visible, key = { it.id }) { kitchen ->
                    RestaurantCard(
                        kitchen = kitchen,
                        onClick = { onKitchenClick(kitchen.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            } else if (hasFilters) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No kitchens match your search",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = viewModel::clearFilters) {
                            Text(
                                text = "Clear filters",
                                style = MaterialTheme.typography.labelLarge,
                                color = SwiggyOrange
                            )
                        }
                    }
                }
            }
        }
    }
}
