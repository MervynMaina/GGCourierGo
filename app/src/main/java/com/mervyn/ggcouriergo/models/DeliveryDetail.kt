package com.mervyn.ggcouriergo.models

data class DeliveryDetail(
    val id: String = "",
    val pickupAddress: String = "",
    val dropoffAddress: String = "",
    val senderName: String = "",
    val receiverName: String = "",
    val receiverPhone: String = "",
    val packageDetails: String = "",
    val status: String = "",
    val assignedDriver: String? = null
)