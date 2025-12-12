package com.mervyn.ggcouriergo.models

data class ParcelTracking(
    val id: String = "",
    val status: String = "",
    val currentLocation: String = "",
    val assignedDriver: String? = null
)
