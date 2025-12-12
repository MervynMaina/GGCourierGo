package com.mervyn.ggcouriergo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mervyn.ggcouriergo.models.DeliveryDetail
import com.mervyn.ggcouriergo.models.DeliveryDetailUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DeliveryDetailViewModel(private val deliveryId: String) : ViewModel() {

    private val _uiState =
        MutableStateFlow<DeliveryDetailUIState>(DeliveryDetailUIState.Loading)
    val uiState: StateFlow<DeliveryDetailUIState> = _uiState

    private val db = FirebaseFirestore.getInstance()

    init {
        loadDelivery()
    }

    private fun loadDelivery() {
        _uiState.value = DeliveryDetailUIState.Loading

        viewModelScope.launch {
            try {
                val doc = db.collection("deliveries").document(deliveryId).get().await()

                if (doc.exists()) {
                    val detail = DeliveryDetail(
                        id = doc.id,
                        pickupAddress = doc.getString("pickupAddress") ?: "",
                        dropoffAddress = doc.getString("dropoffAddress") ?: "",
                        senderName = doc.getString("senderName") ?: "",
                        receiverName = doc.getString("receiverName") ?: "",
                        receiverPhone = doc.getString("receiverPhone") ?: "",
                        packageDetails = doc.getString("packageDetails") ?: "",
                        status = doc.getString("status") ?: "",
                        assignedDriver = doc.getString("assignedDriver")
                    )
                    _uiState.value = DeliveryDetailUIState.Success(detail)

                } else {
                    _uiState.value =
                        DeliveryDetailUIState.Error("Delivery record not found")
                }

            } catch (e: Exception) {
                _uiState.value =
                    DeliveryDetailUIState.Error(e.message ?: "Failed to load delivery details")
            }
        }
    }
}

class DeliveryDetailViewModelFactory(private val deliveryId: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeliveryDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeliveryDetailViewModel(deliveryId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
