package com.mervyn.ggcouriergo.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import com.mervyn.ggcouriergo.models.Parcel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ParcelRepository {

    private val db = FirebaseFirestore.getInstance()
    private val parcelsCollection = db.collection("parcels")

    /**
     * REAL-TIME FLOW: For New/Pending parcels.
     */
    fun getPendingParcelsFlow(): Flow<List<Parcel>> {
        return parcelsCollection
            .whereEqualTo("status", "pending")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    mapParcel(doc.id, doc.data ?: emptyMap())
                }.filter {
                    it.assignedDriver == "UNASSIGNED" || it.assignedDriver.isNullOrBlank()
                }
            }
    }

    /**
     * FIX: Added missing function for Assigned Parcels Tab.
     * Listens for parcels that are assigned, picked up, or in transit.
     */
    fun getAllAssignedParcelsFlow(): Flow<List<Parcel>> {
        return parcelsCollection
            .whereIn("status", listOf("assigned", "picked_up", "in_transit", "delivered"))
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    mapParcel(doc.id, doc.data ?: emptyMap())
                }
            }
    }

    suspend fun addParcel(parcel: Parcel): Boolean {
        return try {
            val parcelMap = hashMapOf(
                "senderName" to parcel.senderName,
                "receiverName" to parcel.receiverName,
                "receiverPhone" to parcel.receiverPhone,
                "pickupAddress" to parcel.pickupAddress,
                "dropoffAddress" to parcel.dropoffAddress,
                "packageDetails" to parcel.packageDetails,
                "status" to "pending",
                "assignedDriver" to "UNASSIGNED",
                "createdAt" to System.currentTimeMillis()
            )
            parcelsCollection.add(parcelMap).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun mapParcel(id: String, data: Map<String, Any>): Parcel {
        return Parcel(
            id = id,
            senderName = data["senderName"] as? String ?: "",
            receiverName = data["receiverName"] as? String ?: "",
            receiverPhone = data["receiverPhone"] as? String ?: "",
            pickupAddress = data["pickupAddress"] as? String ?: "",
            dropoffAddress = data["dropoffAddress"] as? String ?: "",
            packageDetails = data["packageDetails"] as? String ?: "",
            status = data["status"] as? String ?: "pending",
            assignedDriver = data["assignedDriver"] as? String,
            createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )
    }

    suspend fun assignDriver(parcelId: String, driverId: String): Boolean {
        return try {
            db.collection("parcels")
                .document(parcelId)
                .update(
                    "assignedDriver", driverId,
                    "status", "assigned" // Changes status so it moves to the Assigned tab
                )
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getDriverParcelsFlow(driverId: String): Flow<List<Parcel>> {
        return parcelsCollection
            .whereEqualTo("assignedDriver", driverId)
            .whereIn("status", listOf("assigned", "picked_up", "in_transit", "delivered"))
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    mapParcel(doc.id, doc.data ?: emptyMap())
                }
            }
    }

    suspend fun updateParcelStatus(parcelId: String, newStatus: String): Boolean {
        return try {
            parcelsCollection.document(parcelId)
                .update("status", newStatus)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getParcel(parcelId: String): Parcel? {
        return try {
            val document = parcelsCollection.document(parcelId).get().await()
            if (document.exists()) {
                mapParcel(document.id, document.data ?: emptyMap())
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveToHistory(userId: String, trackingId: String) {
        val db = FirebaseFirestore.getInstance()
        val historyData = mapOf(
            "trackingId" to trackingId,
            "timestamp" to FieldValue.serverTimestamp()
        )

        // Save under: users -> {userId} -> history -> {trackingId}
        db.collection("users")
            .document(userId)
            .collection("history")
            .document(trackingId)
            .set(historyData, SetOptions.merge())
    }

}