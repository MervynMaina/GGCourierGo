package com.mervyn.ggcouriergo.models

data class DriverShift(
    val driverId: String = "",
    val isActive: Boolean = false,
    val shiftStartTime: Long? = null,
    val accumulatedTime: Long = 0L // Total time worked in milliseconds
)

sealed class DriverShiftUIState {
    object Loading : DriverShiftUIState()
    data class Success(val shift: DriverShift) : DriverShiftUIState()
    data class Error(val message: String) : DriverShiftUIState()
}