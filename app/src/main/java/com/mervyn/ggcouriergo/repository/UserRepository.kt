package com.mervyn.ggcouriergo.repository

import com.google.firebase.firestore.FirebaseFirestore

class UserRepository(private val db: FirebaseFirestore) {
    fun getUserRole(uid: String, onResult: (Result<String>) -> Unit) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val role = doc.getString("role")
                if (role != null) onResult(Result.success(role))
                else onResult(Result.failure(Exception("Role not found")))
            }
            .addOnFailureListener { e -> onResult(Result.failure(e)) }
    }
}
