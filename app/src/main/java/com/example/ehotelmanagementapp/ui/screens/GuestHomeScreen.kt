package com.example.ehotelmanagementapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.ehotelmanagementapp.viewmodel.RoomState
import com.example.ehotelmanagementapp.viewmodel.RoomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestHomeScreen(
    navController: NavController,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val roomsState by viewModel.roomsState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Available Rooms") },
                actions = {
                    TextButton(onClick = { navController.navigate(Screen.Login.route) }) {
                        Text("Login")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Welcome Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Welcome to eHotel",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Browse our available rooms below",
                    style = MaterialTheme.typography.bodyLarge
                )

            }
        }
        // Available Rooms Section
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
                val availableRooms = state.rooms.filter { it.status == RoomStatus.AVAILABLE }
                RoomsList(
                    rooms = state.rooms,
                    onBookingClick = {
                        // Redirect to login when trying to book
                        navController.navigate(Screen.Login.route)
                    }
                )
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
}
