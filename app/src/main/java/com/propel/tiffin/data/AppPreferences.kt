package com.propel.tiffin.data

import android.content.Context
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("tiffin_prefs")

class AppPreferences(private val context: Context) {

    private object Keys {
        val IS_PAID = booleanPreferencesKey("is_paid")
        val LAUNCH_COUNT = intPreferencesKey("launch_count")
        val TRIAL_STARTED_AT = longPreferencesKey("trial_started_at")
    }

    private val safeData = context.dataStore.data.catch { e ->
        if (e is IOException) emit(emptyPreferences()) else throw e
    }

    val isPaid: Flow<Boolean> = safeData.map { it[Keys.IS_PAID] ?: false }

    val launchCount: Flow<Int> = safeData.map { it[Keys.LAUNCH_COUNT] ?: 0 }

    val trialStartedAt: Flow<Long> = safeData.map { it[Keys.TRIAL_STARTED_AT] ?: 0L }

    suspend fun incrementLaunchCount(): Int {
        var newCount = 0
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.LAUNCH_COUNT] ?: 0
            newCount = current + 1
            prefs[Keys.LAUNCH_COUNT] = newCount
        }
        return newCount
    }

    suspend fun markPaid(trialStartedAtMillis: Long) {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_PAID] = true
            prefs[Keys.TRIAL_STARTED_AT] = trialStartedAtMillis
        }
    }
}
