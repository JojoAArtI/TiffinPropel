package com.propel.tiffin.analytics

sealed interface AnalyticsEvent {
    data object ListViewed : AnalyticsEvent
    data class KitchenViewed(val kitchenId: String) : AnalyticsEvent
    data class PaywallShown(val trigger: String) : AnalyticsEvent
    data object PurchaseCompleted : AnalyticsEvent
}

interface Analytics {
    fun track(event: AnalyticsEvent)
}
