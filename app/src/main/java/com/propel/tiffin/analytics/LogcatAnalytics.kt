package com.propel.tiffin.analytics

import android.util.Log

class LogcatAnalytics : Analytics {

    override fun track(event: AnalyticsEvent) {
        val message = when (event) {
            is AnalyticsEvent.ListViewed -> "ListViewed"
            is AnalyticsEvent.KitchenViewed -> "KitchenViewed kitchenId=${event.kitchenId}"
            is AnalyticsEvent.PaywallShown -> "PaywallShown trigger=${event.trigger}"
            is AnalyticsEvent.PurchaseCompleted -> "PurchaseCompleted"
        }
        Log.d(TAG, message)
    }

    companion object {
        private const val TAG = "TiffinAnalytics"
    }
}
