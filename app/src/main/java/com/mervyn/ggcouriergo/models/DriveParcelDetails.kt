package com.mervyn.ggcouriergo.models

data class DriverParcelDetails(
    val id: String = "",
    val pickupAddress: String = "",
    val dropoffAddress: String = "",
    val receiverName: String = "",
    val receiverPhone: String = "",
    val packageDetails: String = "",
    val status: String = ""
)
