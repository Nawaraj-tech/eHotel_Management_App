package com.example.ehotelmanagementapp.model

import java.util.Date

data class Booking(
    val id: String = "",
    val userId: String = "",
    val roomId: String = "",
    val roomNumber:String = "",
    val roomType: RoomType= RoomType.STANDARD, // Added roomType
    val checkInDate: Date = Date(),
    val checkOutDate: Date = Date(),
    val totalPrice: Double = 0.0,
    val status: BookingStatus = BookingStatus.PENDING,
    val guestName:String = " ",
    val createdAt: Date = Date(),
    val paymentId: String = ""
)

enum class BookingStatus {
    PENDING, // Initial state when booking is made
    CONFIRMED, // After payment/confirmation
    CHECKED_IN, // Guest has arrived
    CHECKED_OUT, // Guest has left
    CANCELLED, // Booking was cancelled
    PAID,

}
