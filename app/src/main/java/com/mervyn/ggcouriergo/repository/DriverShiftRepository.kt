package com.mervyn.ggcouriergo.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import com.mervyn.ggcouriergo.models.DriverShift
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class DriverShiftRepository {
    private val db = FirebaseFirestore.getInstance()
    private val shiftsCollection = db.collection("driver_shifts")

    // Placeholder for getting the current driver ID (In a real app, this comes from FirebaseAuth)
    private val currentDriverId = "driver_123"

    /**
     * Provides a real-time stream of the driver's current shift status.
     */
    fun getShiftStatus(): Flow<DriverShift> = shiftsCollection.document(currentDriverId)
        .snapshots()
        .map { doc ->
            if (doc.exists()) {
                DriverShift(
                    driverId = currentDriverId,
                    isActive = doc.getBoolean("isActive") ?: false,
                    shiftStartTime = doc.getLong("shiftStartTime"),
                    accumulatedTime = doc.getLong("accumulatedTime") ?: 0L
                )
            } else {
                // Return default state if document doesn't exist
                DriverShift(currentDriverId, false, null, 0L)
            }
        }
        .catch {
            // Emit a safe default state on error
            emit(DriverShift(currentDriverId, false, null, 0L))
        }

    /**
     * Toggles the shift status (Clock In or Clock Out).
     */
    suspend fun toggleShift(currentShift: DriverShift): Boolean {
        val now = System.currentTimeMillis()
        val updateMap = mutableMapOf<String, Any?>()

        if (currentShift.isActive) {
            // Clocking OUT: Calculate duration and update accumulated time
            val duration = now - (currentShift.shiftStartTime ?: now)
            val newAccumulatedTime = currentShift.accumulatedTime + duration

            updateMap["isActive"] = false
            updateMap["shiftStartTime"] = null
            updateMap["accumulatedTime"] = newAccumulatedTime
        } else {
            // Clocking IN
            updateMap["isActive"] = true
            updateMap["shiftStartTime"] = now
        }

        return try {
            shiftsCollection.document(currentDriverId).set(updateMap, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            // Log error in a real app
            false
        }
    }
}