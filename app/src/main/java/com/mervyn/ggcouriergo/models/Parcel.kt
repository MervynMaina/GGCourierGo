package com.mervyn.ggcouriergo.models

data class Parcel (
    val id: String = "",
    val senderName: String = "",
    val receiverName: String = "",
    val receiverPhone: String = "",
    val pickupAddress: String = "",
    val dropoffAddress: String = "",
    val packageDetails: String = "",
    val status: String = "pending", // Set default status for consistency
    val assignedDriver: String? = null,
    val createdAt: Long? = null,
    val deliveredAt: Long? = null,
    val deliveryPhotoUrl: String? = null //Cloudinary
)