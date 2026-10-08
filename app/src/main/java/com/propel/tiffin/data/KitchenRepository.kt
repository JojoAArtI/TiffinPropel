package com.propel.tiffin.data

import android.content.Context
import com.propel.tiffin.data.model.Kitchen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.IOException

class KitchenRepository(
    private val context: Context,
    private val json: Json
) {
    var forceError: Boolean = false

    private suspend fun loadAll(): List<Kitchen> = withContext(Dispatchers.IO) {
        val raw = context.assets.open("kitchens.json").bufferedReader().use { it.readText() }
        json.decodeFromString<List<Kitchen>>(raw)
    }

    suspend fun getKitchens(): List<Kitchen> = withContext(Dispatchers.IO) {
        delay(600)
        if (forceError) throw IOException("Simulated load failure")
        loadAll()
    }

    suspend fun getKitchen(id: String): Kitchen? = withContext(Dispatchers.IO) {
        delay(300)
        loadAll().find { it.id == id }
    }
}
