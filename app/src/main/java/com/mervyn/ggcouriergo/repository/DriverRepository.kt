package com.mervyn.ggcouriergo.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mervyn.ggcouriergo.models.Driver
import com.mervyn.ggcouriergo.models.DriverStatus
import kotlinx.coroutines.tasks.await

class DriverRepository {

    private val db = FirebaseFirestore.getInstance()
    private val driversCollection = db.collection("users") // Assuming drivers are part of the 'users' collection

    /**
     * Fetches all drivers who are currently marked as AVAILABLE or ON_DELIVERY.
     * These are the candidates for parcel assignment.
     */
    suspend fun getAvailableDrivers(): List<Driver> {
        return try {
            val availableStatuses = listOf(DriverStatus.AVAILABLE.name, DriverStatus.ON_DELIVERY.name)

            val snapshot = driversCollection
                // Assuming a field 'role' is needed to filter users who are drivers
                .whereEqualTo("role", "driver") // Uncomment if you have role filtering
                .whereIn("status", availableStatuses)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                // We use a general mapper here since we assume 'users' collection stores all fields
                doc.toObject(Driver::class.java)?.copy(id = doc.id)
            }

        } catch (e: Exception) {
            // Log the error (e.g., using Timber or Logcat)
            emptyList()
        }
    }

    /**
     * Fetches a single driver by ID. Needed for display in P2.3 or P3.2.
     */
    suspend fun getDriver(driverId: String): Driver? {
        return try {
            val snapshot = driversCollection.document(driverId).get().await()
            snapshot.toObject(Driver::class.java)?.copy(id = snapshot.id)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Updates the status of a driver. Used when clocking in/out or starting a delivery.
     */
    suspend fun updateDriverStatus(driverId: String, status: DriverStatus): Boolean {
        return try {
            driversCollection.document(driverId)
                .update("status", status.name)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}