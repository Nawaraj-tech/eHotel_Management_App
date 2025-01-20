package com.example.ehotelmanagementapp.model

import androidx.annotation.DrawableRes
import com.example.ehotelmanagementapp.R

data class Room(
    val id: String = "",
    val number: String = "",
    val type: RoomType = RoomType.STANDARD,
    val status: RoomStatus = RoomStatus.AVAILABLE,
    val pricePerNight: Double = 0.0,
    val features: List<String> = emptyList(),
    val description: String = ""
)

enum class RoomStatus {
    AVAILABLE,
    OCCUPIED,
    CLEANING,
    DO_NOT_DISTURB
}

enum class RoomType(
    val title: String,
    val basePrice: Double,
    @DrawableRes val defaultImageRes: Int,
    val description: String

) {
    STANDARD(
        title = "Standard Room",
        basePrice = 89.99,
        defaultImageRes = R.drawable.room_standard,
        description = "Comfortable room with essential amenities"
    ),
    DELUXE(
        title = "Deluxe Room",
        basePrice = 149.99,
        defaultImageRes = R.drawable.room_deluxe,
        description = "Spacious room with premium amenities"
    ),
    SUITE(
        title = "Deluxe Room",
        basePrice = 149.99,
        defaultImageRes = R.drawable.room_deluxe,
        description = "Spacious room with premium amenities"
    )

}
