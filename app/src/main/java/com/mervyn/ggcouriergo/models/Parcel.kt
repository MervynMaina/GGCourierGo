package com.mervyn.ggcouriergo.models

data class Parcel(
    val id: String = "",
    val senderName: String = "",
    val receiverName: String = "",
    val pickupAddress: String = "",
    val dropoffAddress: String = "",
    val packageDetails: String = "",
    val status: String = "",
    val assignedDriver: String? = null,
    val createdAt: Long? = null,       // optional timestamp for ordering
    val deliveredAt: Long? = null,     // timestamp when delivered
    val deliveryPhotoUrl: String? = null // Cloudinary URL
)
