package com.mervyn.ggcouriergo.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.mervyn.ggcouriergo.models.Parcel
import kotlinx.coroutines.flow.Flow // NEW: Import Flow
import kotlinx.coroutines.flow.map    // NEW: Import map for Flow transformations
import kotlinx.coroutines.tasks.await

class ParcelRepository {

    private val db = FirebaseFirestore.getInstance()
    private val parcelsCollection = db.collection("parcels")

    // ----------------------------------------------------------------------
    // REAL-TIME FLOWS (For instantaneous UI updates on main dashboards)
    // ----------------------------------------------------------------------

    // Fetch unassigned parcels (REAL-TIME)
    fun getUnassignedParcelsFlow(): Flow<List<Parcel>> {
        return parcelsCollection
            .whereEqualTo("assignedDriver", "UNASSIGNED")
            .whereEqualTo("status", "pending")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots() // Listen for real-time updates
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.data?.let { data ->
                        mapParcel(doc.id, data)
                    }
                }
            }
    }

    // Fetch all parcels assigned to ANY driver (REAL-TIME)
    fun getAllAssignedParcelsFlow(): Flow<List<Parcel>> {
        return parcelsCollection
            .whereEqualTo("status", "assigned")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots() // Listen for real-time updates
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.data?.let { data ->
                        mapParcel(doc.id, data)
                    }
                }
            }
    }

    // ----------------------------------------------------------------------
    // SUSPEND FUNCTIONS (For single-shot actions like details or forms)
    // ----------------------------------------------------------------------

    // Fetch a single parcel by ID
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

    // Fetch parcels assigned to a specific driver
    suspend fun getAssignedParcels(driverId: String): List<Parcel> {
        return try {
            val snapshot = parcelsCollection
                .whereEqualTo("assignedDriver", driverId)
                .whereEqualTo("status", "assigned")
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
                        "status" to "assigned"
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

    // Legacy function for client filter (kept as suspend)
    suspend fun getAllParcelsForClientFilter(): List<Parcel> {
        return try {
            val snapshot = parcelsCollection
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

    // ------------------------------------------------------------
    // Helper mapper (used internally for consistent data retrieval)
    // ------------------------------------------------------------
    private fun mapParcel(id: String, data: Map<String, Any>): Parcel {
        val createdAtValue = data["createdAt"]
        val createdAtLong = when (createdAtValue) {
            is Long -> createdAtValue
            is Number -> createdAtValue.toLong()
            else -> System.currentTimeMillis()
        }

        return Parcel(
            id = id,
            senderName = data["senderName"] as? String ?: "",
            receiverName = data["receiverName"] as? String ?: "",
            pickupAddress = data["pickupAddress"] as? String ?: "",
            dropoffAddress = data["dropoffAddress"] as? String ?: "",
            packageDetails = data["packageDetails"] as? String ?: "",
            status = data["status"] as? String ?: "pending",
            assignedDriver = data["assignedDriver"] as? String,
            createdAt = createdAtLong
        )
    }
}