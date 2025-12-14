package com.mervyn.ggcouriergo.models

import com.mervyn.ggcouriergo.models.Parcel

sealed class AssignedParcelsUIState {
    object Loading : AssignedParcelsUIState()
    data class Success(val parcels: List<Parcel>) : AssignedParcelsUIState()
    data class Error(val message: String) : AssignedParcelsUIState()
}