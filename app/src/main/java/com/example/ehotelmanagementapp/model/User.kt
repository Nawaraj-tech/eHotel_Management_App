package com.example.ehotelmanagementapp.model

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val phoneNumber: String = ""
)

enum class UserRole {
    STAFF,
    CUSTOMER,
    GUEST
}