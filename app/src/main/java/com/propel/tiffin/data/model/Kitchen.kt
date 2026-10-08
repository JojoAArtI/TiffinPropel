package com.propel.tiffin.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Kitchen(
    val id: String,
    val name: String,
    val cuisine: String,
    val pricePerTiffin: Int,
    val veg: Boolean,
    val rating: Double,
    val weeklyMenu: List<MenuItem>
)

@Serializable
data class MenuItem(val day: String, val dish: String)
