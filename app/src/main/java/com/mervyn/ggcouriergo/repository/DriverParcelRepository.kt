package com.mervyn.ggcouriergo.repository

import android.content.Context
import android.net.Uri
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
                deliveredAt = doc.getLong("deliveredAt"),
                deliveryPhotoUrl = doc.getString("deliveryPhotoUrl")
            )
        } catch (e: Exception) {
            null
        }
    }

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

    // REAL CLOUDINARY INTEGRATION
    suspend fun completeDelivery(parcelId: String, imageUri: Uri, context: Context): Boolean {
        return try {
            // 1. Upload to Cloudinary
            val imageRepo = ImageRepository(context)
            val uploadedUrl = imageRepo.uploadProofOfDelivery(imageUri)

            if (uploadedUrl != null) {
                // 2. Update Firestore with REAL data
                db.collection("parcels")
                    .document(parcelId)
                    .update(
                        mapOf(
                            "status" to "delivered",
                            "deliveryPhotoUrl" to uploadedUrl,
                            "deliveredAt" to System.currentTimeMillis()
                        )
                    ).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}