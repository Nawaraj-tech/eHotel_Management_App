package com.example.ehotelmanagementapp.model

data class Booking(
    val id: String = "",
    val userId: String = "",
    val roomId: String = "",
    val checkInDate: Long = 0,
    val checkOutDate: Long = 0,
    val totalPrice: Double = 0.0,
    val status: BookingStatus = BookingStatus.PENDING,
    val paymentId: String = ""
)

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}
