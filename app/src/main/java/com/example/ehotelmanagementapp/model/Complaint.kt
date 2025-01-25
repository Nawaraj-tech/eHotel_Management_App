package com.example.ehotelmanagementapp.model

data class Complaint(
    val id: String = "",
    val userId: String = "",
    val bookingId:String = "",
    val roomId: String = "",
    val roomNumber: String = "",
    val title: String = "",
    val description: Any? = "",
    val status: ComplaintStatus = ComplaintStatus.OPEN,
    val createdAt: Long = 0,
    val resolvedAt: Long? = null
)

enum class ComplaintStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED
}
