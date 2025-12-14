package com.mervyn.ggcouriergo.models

data class Driver(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val vehiclePlate: String = "",
    val phone: String = "",
    // Status to determine if they can be assigned a new parcel
    val status: DriverStatus = DriverStatus.OFF_DUTY,
    // Optional field for real-time location (future feature)
    val lastKnownLat: Double? = null,
    val lastKnownLng: Double? = null
)

enum class DriverStatus {
    OFF_DUTY,      // Not working, cannot be assigned
    AVAILABLE,     // Working, ready for new assignment
    ON_DELIVERY,   // Currently delivering, may be assigned if queue is empty
    UNAVAILABLE    // Signed in but currently busy/on break
}