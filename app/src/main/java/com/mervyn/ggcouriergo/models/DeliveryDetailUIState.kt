package com.mervyn.ggcouriergo.models

sealed class DeliveryDetailUIState {
    object Loading : DeliveryDetailUIState()
    data class Success(val detail: DeliveryDetail) : DeliveryDetailUIState()
    data class Error(val message: String) : DeliveryDetailUIState()
}