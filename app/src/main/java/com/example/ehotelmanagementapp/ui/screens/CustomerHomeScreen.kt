package com.example.ehotelmanagementapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ehotelmanagementapp.model.RoomStatus
import com.example.ehotelmanagementapp.navigation.Screen
import com.example.ehotelmanagementapp.ui.components.RoomsList
import com.example.ehotelmanagementapp.viewmodel.AuthState
import com.example.ehotelmanagementapp.viewmodel.AuthViewModel
import com.example.ehotelmanagementapp.viewmodel.RoomState
import com.example.ehotelmanagementapp.viewmodel.RoomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    navController: NavController,
    viewModel: RoomViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {

    Log.d("CustomerHomeScreen", "CustomerHomeScreen Composition started")

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val roomsState by viewModel.roomsState.collectAsState()
    val authState by authViewModel.authState.collectAsState()

    // Check auth state when screen is first composed
    LaunchedEffect(Unit) {
        Log.d("CustomerHomeScreen", "Checking current auth state")
        authViewModel.getCurrentAuthState()
    }


    // Monitor auth state changes

    LaunchedEffect(authState) {
        when (authState) {
          /*  AuthState.Initial -> {
                Log.d("CustomerHomeScreen", "Auth state is Initial , navigation to login")
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }*/

            is AuthState.Error -> {
                Log.d("CustomerHomeScreen", "Auth state is Error, navigating to login")
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }

            is AuthState.Success -> {
                // Stay on current Screen
                Log.d("CustomerHomeScreen", "Auth state is Success, staying on screen")

            }

            else -> {}
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Customer Dashboard") }, actions = {
            IconButton(onClick = {
                authViewModel.signOut()
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }) {
                Icon(Icons.Default.ExitToApp, contentDescription = "logout")
            }


        })
    }, bottomBar = {
        NavigationBar {
            NavigationBarItem(selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                icon = { Icon(Icons.Default.Home, "Rooms") },
                label = { Text("Rooms") })
            NavigationBarItem(selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                icon = { Icon(Icons.Default.DateRange, "Bookings") },
                label = { Text("My Bookings") })
            NavigationBarItem(selected = selectedTabIndex == 2,
                onClick = { selectedTabIndex = 2 },
                icon = { Icon(Icons.Default.Email, "Complaints") },
                label = { Text("Complaints") })
        }
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTabIndex) {
                0 -> {
                    // Available Rooms Tab
                    when (val state = roomsState) {
                        is RoomState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        is RoomState.Success -> {
                            val availableRooms = state.rooms.filter {
                                it.status == RoomStatus.AVAILABLE
                            }
                            RoomsList(rooms = availableRooms, onBookingClick = { roomId ->
                                navController.navigate("booking/$roomId")
                            })
                        }

                        is RoomState.Error -> {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                }

                1 -> {
                    // Bookings Tab
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "My Bookings",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    // TODo : Add BookingList Component

                }

                2 -> {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "My Complaints",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

            }

        }
    }
}