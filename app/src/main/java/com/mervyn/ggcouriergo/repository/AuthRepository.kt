package com.mervyn.ggcouriergo.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.mervyn.ggcouriergo.models.LoginUIState
import com.mervyn.ggcouriergo.models.RegisterUIState

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Authenticates the user and fetches their role to determine the correct LoginUIState.
     */
    suspend fun login(email: String, password: String): LoginUIState {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return LoginUIState.Error("User ID not found after login.")
            val doc = db.collection("users").document(uid).get().await()

            val role = doc.getString("role")?.lowercase() ?: "driver"

            when (role) {
                "driver" -> LoginUIState.SuccessDriver
                "dispatcher" -> LoginUIState.SuccessDispatcher
                "admin" -> LoginUIState.SuccessAdmin
                else -> LoginUIState.Error("Login success, but failed to map role: $role")
            }
        } catch (e: Exception) {
            LoginUIState.Error(e.message ?: "Login failed due to network or credentials.")
        }
    }

    suspend fun registerUser(email: String, password: String, name: String, role: String): RegisterUIState {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return RegisterUIState.Error("User ID missing after registration.")

            val assignedRole = role.lowercase().ifBlank { "driver" }

            val userMap = hashMapOf(
                "uid" to uid,
                "email" to email,
                "name" to name,
                "role" to assignedRole
            )

            db.collection("users").document(uid).set(userMap).await()

            RegisterUIState.Success
        } catch (e: Exception) {
            RegisterUIState.Error(e.message ?: "Registration failed.")
        }
    }

    /**
     * Sends a password reset email via Firebase Auth.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}