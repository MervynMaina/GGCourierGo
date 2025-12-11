package com.mervyn.ggcouriergo.repository

class AdminRepository {
    // For now, minimal. Could fetch stats, drivers, dispatchers later
    fun getDashboardStats(): Map<String, Int> {
        return mapOf(
            "drivers" to 8,
            "dispatchers" to 3,
            "deliveriesToday" to 42
        )
    }
}