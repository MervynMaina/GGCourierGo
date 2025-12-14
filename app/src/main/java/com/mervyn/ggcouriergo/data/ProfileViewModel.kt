package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mervyn.ggcouriergo.models.UserProfile
import com.mervyn.ggcouriergo.models.ProfileUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow<ProfileUIState>(ProfileUIState.Loading)
    val uiState: StateFlow<ProfileUIState> = _uiState

    init {
        loadProfile()
    }

    fun loadProfile() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            _uiState.value = ProfileUIState.Error("User not logged in.")
            return
        }

        _uiState.value = ProfileUIState.Loading
        viewModelScope.launch {
            try {
                val doc = db.collection("users").document(uid).get().await()

                if (doc.exists()) {
                    val profile = UserProfile(
                        email = doc.getString("email") ?: auth.currentUser?.email ?: "N/A",
                        role = doc.getString("role") ?: "Unknown",
                        name = doc.getString("name") ?: "New User"
                    )
                    _uiState.value = ProfileUIState.Success(profile)
                } else {
                    _uiState.value = ProfileUIState.Error("Profile not found in Firestore.")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUIState.Error(e.message ?: "Failed to load profile data.")
            }
        }
    }

    fun logout(navController: androidx.navigation.NavController) {
        auth.signOut()
        navController.navigate("login") {
            popUpTo("login") { inclusive = true }
        }
    }
}