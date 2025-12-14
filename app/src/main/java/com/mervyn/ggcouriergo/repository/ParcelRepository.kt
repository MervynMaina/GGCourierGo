package com.mervyn.ggcouriergo.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mervyn.ggcouriergo.models.Parcel
import kotlinx.coroutines.tasks.await

class ParcelRepository {

    private val db = FirebaseFirestore.getInstance()
    private val parcelsCollection = db.collection("parcels")

    // Fetch unassigned parcels
    suspend fun getUnassignedParcels(): List<Parcel> {
        return try {
            val snapshot = parcelsCollection
                .whereEqualTo("assignedDriver", null)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.data?.let { data ->
                    mapParcel(doc.id, data)
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Fetch a single parcel by ID (REQUIRED FOR P2.3)
    suspend fun getParcel(parcelId: String): Parcel? {
        return try {
            val snapshot = parcelsCollection.document(parcelId).get().await()
            snapshot.data?.let { data ->
                mapParcel(snapshot.id, data)
            }
        } catch (e: Exception) {
            null
        }
    }

    // NEW: Fetch all parcels assigned to ANY driver (REQUIRED FOR P2.4)
    suspend fun getAllAssignedParcels(): List<Parcel> {
        return try {
            val snapshot = parcelsCollection
                // Firestore requires an index if combining whereNotEqualTo and orderBy on different fields.
                // Assuming "assignedDriver" is not null is generally sufficient for a list of assigned items.
                .whereNotEqualTo("assignedDriver", null)
                .orderBy("assignedDriver", Query.Direction.ASCENDING) // Order by driver first to enable the filter
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.data?.let { data ->
                    mapParcel(doc.id, data)
                }
            }
        } catch (e: Exception) {
            // NOTE: If this throws an error about missing indexes, the developer must create one in Firebase console.
            emptyList()
        }
    }

    // Fetch parcels assigned to a specific driver
    suspend fun getAssignedParcels(driverId: String): List<Parcel> {
        return try {
            val snapshot = parcelsCollection
                .whereEqualTo("assignedDriver", driverId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.data?.let { data ->
                    mapParcel(doc.id, data)
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Assign driver to parcel
    suspend fun assignDriver(parcelId: String, driverId: String): Boolean {
        return try {
            parcelsCollection.document(parcelId)
                .update(
                    mapOf(
                        "assignedDriver" to driverId,
                        "status" to "assigned" // Update status upon assignment
                    )
                )
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Add a new parcel
    suspend fun addParcel(parcel: Parcel): Boolean {
        return try {
            val parcelMap = hashMapOf(
                "senderName" to parcel.senderName,
                "receiverName" to parcel.receiverName,
                "pickupAddress" to parcel.pickupAddress,
                "dropoffAddress" to parcel.dropoffAddress,
                "packageDetails" to parcel.packageDetails,
                "status" to (parcel.status.ifBlank { "pending" }),
                "assignedDriver" to parcel.assignedDriver,
                "createdAt" to (parcel.createdAt ?: System.currentTimeMillis())
            )
            parcelsCollection.add(parcelMap).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // ------------------------------------------------------------
    // Helper mapper (used internally for consistent data retrieval)
    // ------------------------------------------------------------
    private fun mapParcel(id: String, data: Map<String, Any>): Parcel {
        return Parcel(
            id = id,
            senderName = data["senderName"] as? String ?: "",
            receiverName = data["receiverName"] as? String ?: "",
            pickupAddress = data["pickupAddress"] as? String ?: "",
            dropoffAddress = data["dropoffAddress"] as? String ?: "",
            packageDetails = data["packageDetails"] as? String ?: "",
            status = data["status"] as? String ?: "pending",
            assignedDriver = data["assignedDriver"] as? String,
            createdAt = (data["createdAt"] as? Long) ?: System.currentTimeMillis()
        )
    }
}