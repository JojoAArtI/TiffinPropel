package com.propel.tiffin.ui.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.propel.tiffin.analytics.Analytics
import com.propel.tiffin.analytics.AnalyticsEvent
import com.propel.tiffin.data.AppPreferences
import com.propel.tiffin.di.AppContainer
import com.propel.tiffin.util.trialChargeDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PaywallViewModel(
    private val prefs: AppPreferences,
    private val analytics: Analytics,
    private val trigger: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaywallUiState>(PaywallUiState.Offer(""))
    val uiState: StateFlow<PaywallUiState> = _uiState

    private var paywallShownFired = false

    init {
        viewModelScope.launch {
            val paid = prefs.isPaid.first()
            if (paid) {
                _uiState.value = PaywallUiState.AlreadyPaid
            } else {
                val chargeDate = trialChargeDate(System.currentTimeMillis())
                _uiState.value = PaywallUiState.Offer(chargeDate)
                if (!paywallShownFired) {
                    paywallShownFired = true
                    analytics.track(AnalyticsEvent.PaywallShown(trigger))
                }
            }
        }
    }

    fun confirmPurchase() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            prefs.markPaid(now)
            analytics.track(AnalyticsEvent.PurchaseCompleted)
            val chargeDate = trialChargeDate(now)
            _uiState.value = PaywallUiState.JustPurchased(chargeDate)
        }
    }
}

class PaywallViewModelFactory(
    private val container: AppContainer,
    private val trigger: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PaywallViewModel(
            container.appPreferences,
            container.analytics,
            trigger
        ) as T
    }
}
