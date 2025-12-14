package com.mervyn.ggcouriergo.models

data class DeliverySummary(
    val id: String = "",
    val pickupAddress: String = "",
    val dropoffAddress: String = "",
    val receiverName: String = "",
    val receiverPhone: String = "",
    val packageDetails: String = "",
    val deliveredAt: Long? = null,
    val deliveryPhotoUrl: String? = null // ADDED: Critical for Proof of Delivery (POD)
)