package com.propel.tiffin.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.propel.tiffin.analytics.Analytics
import com.propel.tiffin.analytics.AnalyticsEvent
import com.propel.tiffin.data.KitchenRepository
import com.propel.tiffin.data.model.Kitchen
import com.propel.tiffin.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListViewModel(
    private val repo: KitchenRepository,
    private val analytics: Analytics
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListUiState>(ListUiState.Loading)
    val uiState: StateFlow<ListUiState> = _uiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _allKitchens = MutableStateFlow<List<Kitchen>>(emptyList())

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _selectedCuisine = MutableStateFlow<String?>(null)
    val selectedCuisine: StateFlow<String?> = _selectedCuisine

    val cuisines: StateFlow<List<Pair<String, String>>> = _allKitchens
        .combine(MutableStateFlow(Unit)) { kitchens, _ ->
            kitchens.distinctBy { it.cuisine }
                .map { it.cuisine to it.imageAsset }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val visibleKitchens: StateFlow<List<Kitchen>> = combine(
        _allKitchens, _query, _selectedCuisine
    ) { kitchens, q, cuisine ->
        val trimmed = q.trim()
        kitchens.filter { kitchen ->
            val matchesQuery = trimmed.isEmpty() ||
                kitchen.name.contains(trimmed, ignoreCase = true) ||
                kitchen.cuisine.contains(trimmed, ignoreCase = true)
            val matchesCuisine = cuisine == null || kitchen.cuisine == cuisine
            matchesQuery && matchesCuisine
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hasActiveFilters: StateFlow<Boolean> = combine(_query, _selectedCuisine) { q, c ->
        q.trim().isNotEmpty() || c != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private var listViewedFired = false

    init {
        load()
    }

    fun onQueryChange(q: String) {
        _query.value = q
    }

    fun onCuisineSelected(cuisine: String?) {
        _selectedCuisine.value = if (_selectedCuisine.value == cuisine) null else cuisine
    }

    fun clearFilters() {
        _query.value = ""
        _selectedCuisine.value = null
    }

    fun load() {
        _uiState.value = ListUiState.Loading
        viewModelScope.launch {
            try {
                val kitchens = repo.getKitchens()
                if (kitchens.isEmpty()) {
                    _uiState.value = ListUiState.Empty
                } else {
                    _allKitchens.value = kitchens
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
                    _allKitchens.value = kitchens
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
