package com.mervyn.ggcouriergo.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.mervyn.ggcouriergo.models.LoginUIState
import com.mervyn.ggcouriergo.models.RegisterUIState

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // LOGIN
    suspend fun login(email: String, password: String): LoginUIState {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return LoginUIState.Error("User ID not found")
            val doc = db.collection("users").document(uid).get().await()

            val role = doc.getString("role")?.lowercase() ?: "driver"

            when (role) {
                "driver" -> LoginUIState.SuccessDriver
                "dispatcher" -> LoginUIState.SuccessDispatcher
                "admin" -> LoginUIState.SuccessAdmin
                else -> LoginUIState.Error("Unknown role: $role")
            }
        } catch (e: Exception) {
            LoginUIState.Error(e.message ?: "Login failed")
        }
    }

    // REGISTER
    suspend fun registerUser(email: String, password: String, name: String): RegisterUIState {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return RegisterUIState.Error("User ID missing")

            val userMap = hashMapOf(
                "uid" to uid,
                "email" to email,
                "name" to name,
                "role" to "driver"   // default role for now
            )

            db.collection("users").document(uid).set(userMap).await()

            RegisterUIState.Success
        } catch (e: Exception) {
            RegisterUIState.Error(e.message ?: "Registration failed")
        }
    }
}
