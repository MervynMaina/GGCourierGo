package com.mervyn.ggcouriergo.models

// Assuming you have a Parcel data class defined elsewhere (e.g., in models/Parcel.kt)

/**
 * Defines the UI states for the list of New (Unassigned) Parcels in the Dispatcher view.
 */
sealed class NewParcelsUIState {
    object Loading : NewParcelsUIState()
    data class Success(val parcels: List<Parcel>) : NewParcelsUIState()
    data class Error(val message: String) : NewParcelsUIState()
    // Optional: Add Empty state if needed, but Success handles size 0.
}