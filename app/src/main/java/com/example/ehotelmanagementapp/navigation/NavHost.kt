package com.example.ehotelmanagementapp.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ehotelmanagementapp.ui.components.ComplaintDialog
import com.example.ehotelmanagementapp.ui.screens.CustomerComplaintsScreen
import com.example.ehotelmanagementapp.ui.screens.CustomerHomeScreen
import com.example.ehotelmanagementapp.ui.screens.GuestHomeScreen
import com.example.ehotelmanagementapp.ui.screens.LoginScreen
import com.example.ehotelmanagementapp.ui.screens.PaymentScreen
import com.example.ehotelmanagementapp.ui.screens.RegisterScreen
import com.example.ehotelmanagementapp.ui.screens.SplashScreen
import com.example.ehotelmanagementapp.ui.screens.StaffComplaintsScreen
import com.example.ehotelmanagementapp.ui.screens.StaffHomeScreen

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
            Log.d("AppNavigation", "navigationg to login Screen")
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }

        // Guest flows
        composable(Screen.GuestHome.route) {
            GuestHomeScreen(navController)
        }
        composable(Screen.CustomerComplaints.route) {
            CustomerComplaintsScreen(navController)
        }

        composable(Screen.StaffComplaints.route) {
            StaffComplaintsScreen(navController = navController)
        }
        /* composable(Screen.RoomList.route) {
             RoomListScreen(navController)
         }*/

        // Customer flows
        composable(Screen.CustomerHome.route) {
            CustomerHomeScreen(navController)
        }
        composable(Screen.StaffHome.route) {
            StaffHomeScreen(navController)
        }

        composable(
            route = Screen.Payment.route,
            arguments = listOf(
                navArgument("bookingId") { type = NavType.StringType },
                navArgument("amount") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            PaymentScreen(
                navController = navController,
                bookingId = backStackEntry.arguments?.getString("bookingId") ?: "",
                amount = backStackEntry.arguments?.getFloat("amount")?.toDouble() ?: 0.0
            )
        }

        composable(route = Screen.AddComplaint.route){
            ComplaintDialog(
                bookings = emptyList(),
                onDismiss = { navController.popBackStack() },
                onSubmit = { booking, title, description ->
                    navController.navigate(Screen.CustomerComplaints.route)
                })

        }
        /*composable(Screen.BookingHistory.route) {
            BookingHistoryScreen(navController)
        }
        composable(Screen.ComplaintsList.route) {
            ComplaintsListScreen(navController)
        }
        composable(Screen.CreateComplaint.route) {
            CreateComplaintScreen(navController)
        }

         Staff flows
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