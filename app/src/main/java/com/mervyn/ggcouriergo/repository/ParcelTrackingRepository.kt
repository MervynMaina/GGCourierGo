package com.mervyn.ggcouriergo.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mervyn.ggcouriergo.models.ParcelTracking
import kotlinx.coroutines.channels.awaitClose // CRITICAL IMPORT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

            ParcelTracking(
                id = doc.id,
                status = doc.getString("status") ?: "pending",
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
            null
        }
    }

    // Save search to user history
    suspend fun saveToHistory(userId: String, trackingId: String) {
        val historyItem = mapOf(
            "trackingId" to trackingId,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("users").document(userId)
            .collection("history").document(trackingId)
            .set(historyItem).await()
    }

    // Get real-time history flow
    fun getTrackingHistory(userId: String): Flow<List<String>> = callbackFlow {
        val subscription = db.collection("users").document(userId)
            .collection("history")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // Closes the flow on error
                    return@addSnapshotListener
                }
                val history = snapshot?.documents?.mapNotNull {
                    it.getString("trackingId")
                } ?: emptyList()

                trySend(history)
            }

        // Properly cleans up the Firebase listener when the flow is closed
        awaitClose { subscription.remove() }
    }

    // Clear all history
    suspend fun clearHistory(userId: String) {
        try {
            val historyRef = db.collection("users").document(userId).collection("history")
            val snapshots = historyRef.get().await()
            for (doc in snapshots.documents) {
                doc.reference.delete().await()
            }
        } catch (e: Exception) {
            // Handle or log potential deletion errors
        }
    }
}