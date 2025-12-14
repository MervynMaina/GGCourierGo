package com.mervyn.ggcouriergo.repository

import User
import com.google.firebase.firestore.FirebaseFirestore
import com.mervyn.ggcouriergo.models.Parcel // <--- REQUIRED IMPORT
import kotlinx.coroutines.tasks.await

class AdminRepository {

    private val db = FirebaseFirestore.getInstance()

    // NEW: Function to fetch all dashboard data
    suspend fun fetchDashboardData(): Result<Pair<List<User>, List<Parcel>>> {
        return try {
            // 1. Fetch Users
            val userSnapshot = db.collection("users").get().await()
            val users = userSnapshot.documents.map { doc ->
                // ERROR 1 & 3 FIXED: User is now imported
                User(
                    id = doc.id,
                    email = doc.getString("email") ?: "",
                    role = doc.getString("role") ?: "driver"
                )
            }

            // 2. Fetch Parcels
            val parcelSnapshot = db.collection("parcels").get().await()
            val parcels = parcelSnapshot.documents.map { doc ->
                // ERROR 2 FIXED: Parcel is now imported
                Parcel(
                    id = doc.id,
                    senderName = doc.getString("senderName") ?: "",
                    receiverName = doc.getString("receiverName") ?: "",
                    // Note: If you need pickupAddress/dropoffAddress here, you need to add them back.
                    // Assuming for the Admin dashboard summary, this simplified Parcel model is okay,
                    // but you should use the full Parcel model for consistency.
                    status = doc.getString("status") ?: ""
                )
            }

            // ERROR 4, 5, 6 FIXED: Kotlin can now infer types because User and Parcel are known classes.
            Result.success(Pair(users, parcels))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}