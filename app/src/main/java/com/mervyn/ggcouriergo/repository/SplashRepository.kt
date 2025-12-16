package com.mervyn.ggcouriergo.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mervyn.ggcouriergo.models.UserRole
import kotlinx.coroutines.tasks.await

class SplashRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getUserRole(): Result<UserRole> {
        val currentUser = auth.currentUser ?: return Result.success(UserRole.NEW_USER)

        return try {
            val doc = db.collection("users").document(currentUser.uid).get().await()
            // Standardize string to lowercase to match the "when" cases safely
            val roleStr = doc.getString("role")?.lowercase() ?: "new_user"

            val role = when (roleStr) {
                "admin" -> UserRole.ADMIN
                "dispatcher" -> UserRole.DISPATCHER
                "driver" -> UserRole.DRIVER
                else -> UserRole.NEW_USER
            }
            Result.success(role)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}