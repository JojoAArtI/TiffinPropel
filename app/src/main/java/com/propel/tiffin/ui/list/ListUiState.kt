package com.propel.tiffin.ui.list

import com.propel.tiffin.data.model.Kitchen

sealed interface ListUiState {
    data object Loading : ListUiState
    data object Empty : ListUiState
    data class Error(val message: String) : ListUiState
    data class Content(val kitchens: List<Kitchen>) : ListUiState
}
