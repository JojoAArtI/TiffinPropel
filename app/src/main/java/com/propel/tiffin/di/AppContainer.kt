package com.propel.tiffin.di

import android.content.Context
import com.propel.tiffin.analytics.Analytics
import com.propel.tiffin.analytics.LogcatAnalytics
import com.propel.tiffin.data.AppPreferences
import com.propel.tiffin.data.KitchenRepository
import kotlinx.serialization.json.Json

class AppContainer(context: Context) {
    val json: Json = Json { ignoreUnknownKeys = true }
    val kitchenRepository: KitchenRepository = KitchenRepository(context, json)
    val appPreferences: AppPreferences = AppPreferences(context)
    val analytics: Analytics = LogcatAnalytics()
}
