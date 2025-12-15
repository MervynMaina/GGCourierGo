package com.mervyn.ggcouriergo.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import com.mervyn.ggcouriergo.models.DriverShift
import com.mervyn.ggcouriergo.models.DriverStatus // ⬅️ NEW IMPORT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

// 💥 FIX: Inject DriverRepository into the constructor
class DriverShiftRepository(private val driverRepository: DriverRepository) {
    private val db = FirebaseFirestore.getInstance()
    private val shiftsCollection = db.collection("driver_shifts")
    private val usersCollection = db.collection("users") // ⬅️ Added reference to users collection

    // Placeholder for getting the current driver ID (In a real app, this comes from FirebaseAuth)
    // NOTE: Replace "driver_123" with the actual logic to fetch the logged-in user's UID.
    private val currentDriverId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("User not logged in.")
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
        val shiftUpdateMap = mutableMapOf<String, Any?>()
        val newDriverStatus: DriverStatus // ⬅️ Variable for the user status update

        if (currentShift.isActive) {
            // Clocking OUT: Calculate duration and update accumulated time
            val duration = now - (currentShift.shiftStartTime ?: now)
            val newAccumulatedTime = currentShift.accumulatedTime + duration

            shiftUpdateMap["isActive"] = false
            shiftUpdateMap["shiftStartTime"] = null
            shiftUpdateMap["accumulatedTime"] = newAccumulatedTime
            newDriverStatus = DriverStatus.OFF_DUTY // ⬅️ Set status for Clock Out
        } else {
            // Clocking IN
            shiftUpdateMap["isActive"] = true
            shiftUpdateMap["shiftStartTime"] = now
            newDriverStatus = DriverStatus.AVAILABLE // ⬅️ Set status for Clock In (Fix for Dispatcher view)
        }

        return try {
            // 1. Update the shift details in 'driver_shifts' collection (Timer data)
            shiftsCollection.document(currentDriverId).set(shiftUpdateMap, SetOptions.merge()).await()

            // 2. 💥 CRITICAL FIX: Update the 'status' field in the 'users' collection
            usersCollection.document(currentDriverId)
                .update("status", newDriverStatus.name)
                .await()

            true
        } catch (e: Exception) {
            // Log error in a real app
            false
        }
    }

    /**
     * NEW: Resets the accumulated time and shift state for a new day.
     */
    suspend fun resetShift(): Boolean {
        return try {
            // This should also ideally set the user status to OFF_DUTY if it succeeds
            shiftsCollection.document(currentDriverId)
                .set(
                    mapOf(
                        "accumulatedTime" to 0L,
                        "shiftStartTime" to null,
                        "isActive" to false
                    ),
                    SetOptions.merge()
                )
                .await()
            // Optional: Update user status to OFF_DUTY here as well
            usersCollection.document(currentDriverId).update("status", DriverStatus.OFF_DUTY.name).await()

            true
        } catch (e: Exception) {
            // Log error in a real app
            false
        }
    }
}