package com.mervyn.ggcouriergo.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mervyn.ggcouriergo.models.DriverParcelDetails
import kotlinx.coroutines.tasks.await

class DriverParcelRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getParcel(parcelId: String): DriverParcelDetails? {
        return try {
            val doc = db.collection("parcels").document(parcelId).get().await()
            if (!doc.exists()) return null

            DriverParcelDetails(
                id = doc.id,
                pickupAddress = doc.getString("pickupAddress") ?: "",
                dropoffAddress = doc.getString("dropoffAddress") ?: "",
                receiverName = doc.getString("receiverName") ?: "",
                receiverPhone = doc.getString("receiverPhone") ?: "",
                packageDetails = doc.getString("packageDetails") ?: "",
                status = doc.getString("status") ?: "",
                // NEW: Map the delivery details
                deliveredAt = doc.getLong("deliveredAt"),
                deliveryPhotoUrl = doc.getString("deliveryPhotoUrl")
            )
        } catch (e: Exception) {
            null
        }
    }

    // NOTE: This repository needs an update status method that accepts the photo URL,
    // but for now, we leave the simple one as the screen simulates the URL.
    suspend fun updateStatus(parcelId: String, newStatus: String): Boolean {
        return try {
            db.collection("parcels").document(parcelId)
                .update("status", newStatus)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}