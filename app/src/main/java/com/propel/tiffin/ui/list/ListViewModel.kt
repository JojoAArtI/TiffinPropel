package com.propel.tiffin.ui.list

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

class ListViewModel(
    private val repo: KitchenRepository,
    private val analytics: Analytics
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListUiState>(ListUiState.Loading)
    val uiState: StateFlow<ListUiState> = _uiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private var listViewedFired = false

    init {
        load()
    }

    fun load() {
        _uiState.value = ListUiState.Loading
        viewModelScope.launch {
            try {
                val kitchens = repo.getKitchens()
                if (kitchens.isEmpty()) {
                    _uiState.value = ListUiState.Empty
                } else {
                    _uiState.value = ListUiState.Content(kitchens)
                    if (!listViewedFired) {
                        listViewedFired = true
                        analytics.track(AnalyticsEvent.ListViewed)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ListUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val kitchens = repo.getKitchens()
                if (kitchens.isEmpty()) {
                    _uiState.value = ListUiState.Empty
                } else {
                    _uiState.value = ListUiState.Content(kitchens)
                }
            } catch (e: Exception) {
                _uiState.value = ListUiState.Error(e.message ?: "Something went wrong")
            }
            _isRefreshing.value = false
        }
    }

    fun retry() {
        repo.forceError = false
        load()
    }

    fun simulateError() {
        repo.forceError = true
        load()
    }
}

class ListViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ListViewModel(container.kitchenRepository, container.analytics) as T
    }
}
