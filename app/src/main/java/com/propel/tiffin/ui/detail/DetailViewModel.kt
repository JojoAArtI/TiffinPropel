package com.propel.tiffin.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.propel.tiffin.analytics.Analytics
import com.propel.tiffin.analytics.AnalyticsEvent
import com.propel.tiffin.data.KitchenRepository
import com.propel.tiffin.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repo: KitchenRepository,
    private val analytics: Analytics,
    private val kitchenId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    private var kitchenViewedFired = false

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val kitchen = repo.getKitchen(kitchenId)
            if (kitchen == null) {
                _uiState.value = DetailUiState.NotFound
            } else {
                _uiState.value = DetailUiState.Content(kitchen)
                if (!kitchenViewedFired) {
                    kitchenViewedFired = true
                    analytics.track(AnalyticsEvent.KitchenViewed(kitchenId))
                }
            }
        }
    }
}

class DetailViewModelFactory(
    private val container: AppContainer,
    private val kitchenId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailViewModel(
            container.kitchenRepository,
            container.analytics,
            kitchenId
        ) as T
    }
}
