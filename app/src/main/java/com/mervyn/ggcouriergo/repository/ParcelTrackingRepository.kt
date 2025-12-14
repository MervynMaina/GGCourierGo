package com.mervyn.ggcouriergo.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mervyn.ggcouriergo.models.ParcelTracking // Using the user-defined model for the final screen
import kotlinx.coroutines.tasks.await

class ParcelTrackingRepository {

    private val db = FirebaseFirestore.getInstance()
    private val parcelsCollection = db.collection("parcels")

    /**
     * Fetches read-only parcel data for customer tracking based on ID.
     */
    suspend fun getTrackingParcel(parcelId: String): ParcelTracking? {
        return try {
            val doc = parcelsCollection.document(parcelId).get().await()

            if (!doc.exists()) return null

            // Map only the necessary fields for a customer view.
            // We use the user-defined ParcelTracking model for the return type.
            ParcelTracking(
                id = doc.id,
                status = doc.getString("status") ?: "pending",
                // CurrentLocation is usually complex, but here we provide a simplified status placeholder
                currentLocation = when (doc.getString("status")?.lowercase()) {
                    "assigned" -> "Awaiting pickup at sender location"
                    "picked_up" -> "In transit to dropoff area"
                    "in_transit" -> "Out for final delivery"
                    "delivered" -> "Delivered to recipient"
                    else -> "Processing center"
                },
                assignedDriver = doc.getString("assignedDriver")
            )
        } catch (e: Exception) {
            // In a real app, log the exception
            null
        }
    }
}