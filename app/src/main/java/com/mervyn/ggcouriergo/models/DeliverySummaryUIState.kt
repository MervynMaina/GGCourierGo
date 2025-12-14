package com.mervyn.ggcouriergo.models

sealed class DeliverySummaryUIState {
    object Loading : DeliverySummaryUIState()
    data class Success(val summary: DeliverySummary) : DeliverySummaryUIState()
    data class Error(val message: String) : DeliverySummaryUIState()
}