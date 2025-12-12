package com.mervyn.ggcouriergo.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mervyn.ggcouriergo.models.Parcel
import kotlinx.coroutines.tasks.await

class ParcelRepository {

    private val db = FirebaseFirestore.getInstance()

    // Fetch unassigned parcels
    suspend fun getUnassignedParcels(): List<Parcel> {
        return try {
            val snapshot = db.collection("parcels")
                .whereEqualTo("assignedDriver", null)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.map { doc ->
                Parcel(
                    id = doc.id,
                    senderName = doc.getString("senderName") ?: "",
                    receiverName = doc.getString("receiverName") ?: "",
                    pickupAddress = doc.getString("pickupAddress") ?: "",
                    dropoffAddress = doc.getString("dropoffAddress") ?: "",
                    status = doc.getString("status") ?: "",
                    assignedDriver = doc.getString("assignedDriver") ?: null,
                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Fetch parcels assigned to a specific driver ✅
    suspend fun getAssignedParcels(driverId: String): List<Parcel> {
        return try {
            val snapshot = db.collection("parcels")
                .whereEqualTo("assignedDriver", driverId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.map { doc ->
                Parcel(
                    id = doc.id,
                    senderName = doc.getString("senderName") ?: "",
                    receiverName = doc.getString("receiverName") ?: "",
                    pickupAddress = doc.getString("pickupAddress") ?: "",
                    dropoffAddress = doc.getString("dropoffAddress") ?: "",
                    status = doc.getString("status") ?: "",
                    assignedDriver = doc.getString("assignedDriver") ?: null,
                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Assign driver to parcel
    suspend fun assignDriver(parcelId: String, driverId: String): Boolean {
        return try {
            db.collection("parcels").document(parcelId)
                .update("assignedDriver", driverId)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Add a new parcel
    suspend fun addParcel(parcel: Parcel): Boolean {
        return try {
            val docRef = db.collection("parcels").document()
            val parcelMap = hashMapOf(
                "senderName" to parcel.senderName,
                "receiverName" to parcel.receiverName,
                "pickupAddress" to parcel.pickupAddress,
                "dropoffAddress" to parcel.dropoffAddress,
                "status" to parcel.status,
                "assignedDriver" to parcel.assignedDriver,
                "createdAt" to (parcel.createdAt ?: System.currentTimeMillis())
            )
            docRef.set(parcelMap).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
