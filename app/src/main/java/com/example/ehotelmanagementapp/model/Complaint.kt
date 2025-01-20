package com.example.ehotelmanagementapp.model

data class Complaint(
    val id: String = "",
    val userId: String = "",
    val roomId: String = "",
    val title: String = "",
    val description: String = "",
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
