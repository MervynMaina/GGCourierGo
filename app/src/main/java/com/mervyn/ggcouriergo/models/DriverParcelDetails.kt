package com.mervyn.ggcouriergo.models

data class DriverParcelDetails(
    val id: String = "",
    val pickupAddress: String = "",
    val dropoffAddress: String = "",
    val receiverName: String = "",
    val receiverPhone: String = "",
    val packageDetails: String = "",
    val status: String = "",
    // NEW FIELDS REQUIRED FOR P3.2/P3.3
    val deliveredAt: Long? = null,
    val deliveryPhotoUrl: String? = null
)