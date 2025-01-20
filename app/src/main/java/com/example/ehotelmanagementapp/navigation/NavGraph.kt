package com.example.ehotelmanagementapp.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")

    // Guest screens
    object GuestHome : Screen("guest_home")
    object RoomList : Screen("room_list")

    // Customer screens
    object CustomerHome : Screen("customer_home")
    object BookingHistory : Screen("booking_history")
    object ComplaintsList : Screen("complaints_list")
    object CreateComplaint : Screen("create_complaint")

    // Staff screens
    object StaffHome : Screen("staff_home")
    object RoomManagement : Screen("room_management")
    object ComplaintsManagement : Screen("complaints_management")
    object AdminPanel : Screen("admin_panel")
}