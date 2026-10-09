package com.propel.tiffin.ui.paywall

sealed interface PaywallUiState {
    data class Offer(val chargeDateText: String) : PaywallUiState
    data object AlreadyPaid : PaywallUiState
    data class JustPurchased(val chargeDate: String) : PaywallUiState
}
