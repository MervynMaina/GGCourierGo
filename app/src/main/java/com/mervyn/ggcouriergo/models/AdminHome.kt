import com.mervyn.ggcouriergo.models.Parcel

data class User(
    val id: String = "",
    val email: String = "",
    val role: String = ""
)

data class Parcels(
    val id: String = "",
    val senderName: String = "",
    val receiverName: String = "",
    val status: String = ""
)

data class AdminDashboardData(
    val users: List<User> = emptyList(),
    val parcels: List<Parcel> = emptyList(),
    val totalParcels: Int = 0,
    val deliveredParcels: Int = 0,
    val pendingParcels: Int = 0
)