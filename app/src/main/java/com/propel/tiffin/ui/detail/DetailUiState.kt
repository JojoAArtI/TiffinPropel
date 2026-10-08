package com.propel.tiffin.ui.detail

import com.propel.tiffin.data.model.Kitchen

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data object NotFound : DetailUiState
    data class Content(val kitchen: Kitchen) : DetailUiState
}
