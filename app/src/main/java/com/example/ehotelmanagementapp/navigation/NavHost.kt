package com.example.ehotelmanagementapp.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ehotelmanagementapp.ui.screens.LoginScreen
import com.example.ehotelmanagementapp.ui.screens.RegisterScreen
import com.example.ehotelmanagementapp.ui.screens.SplashScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentication flows
        composable(Screen.Splash.route) {
            Log.d("AppNavigation", "Navigating to SplashScreen")
            SplashScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }

        // Guest flows
        /* composable(Screen.GuestHome.route) {
            GuestHomeScreen(navController)
        }
        composable(Screen.RoomList.route) {
            RoomListScreen(navController)
        }

        // Customer flows
        composable(Screen.CustomerHome.route) {
            CustomerHomeScreen(navController)
        }
        composable(Screen.BookingHistory.route) {
            BookingHistoryScreen(navController)
        }
        composable(Screen.ComplaintsList.route) {
            ComplaintsListScreen(navController)
        }
        composable(Screen.CreateComplaint.route) {
            CreateComplaintScreen(navController)
        }

        // Staff flows
        composable(Screen.StaffHome.route) {
            StaffHomeScreen(navController)
        }
        composable(Screen.RoomManagement.route) {
            RoomManagementScreen(navController)
        }
        composable(Screen.ComplaintsManagement.route) {
            ComplaintsManagementScreen(navController)
        }
        composable(Screen.AdminPanel.route) {
            AdminPanelScreen(navController)
        }*/
    }
}