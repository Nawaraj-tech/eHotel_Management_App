package com.example.ehotelmanagementapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Call
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
import com.example.ehotelmanagementapp.model.Room
import com.example.ehotelmanagementapp.model.RoomStatus
import com.example.ehotelmanagementapp.model.RoomType
import com.example.ehotelmanagementapp.navigation.Screen
import com.example.ehotelmanagementapp.ui.components.AddRoomDialog
import com.example.ehotelmanagementapp.ui.components.RoomCard
import com.example.ehotelmanagementapp.viewmodel.AuthViewModel
import com.example.ehotelmanagementapp.viewmodel.RoomState
import com.example.ehotelmanagementapp.viewmodel.RoomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffHomeScreen(
    navController: NavController,
    roomViewModel: RoomViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val roomsState by roomViewModel.roomsState.collectAsState()
    var showAddRoomDialog by remember { mutableStateOf(false) }
    // Status update dialog state
    var selectedRoomForStatus by remember { mutableStateOf<Room?>(null) }
    var showStatusDialog by remember { mutableStateOf(false) }
    Scaffold(topBar = {
        TopAppBar(title = { Text("Staff Dashboard") }, actions = {
            IconButton(onClick = {
                authViewModel.signOut()
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }) {
                Icon(Icons.Default.ExitToApp, "Logout")
            }
        })
    },
        bottomBar = {
        NavigationBar {
            NavigationBarItem(selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                icon = { Icon(Icons.Default.Home, "Rooms") },
                label = { Text("Rooms") })
            NavigationBarItem(selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                icon = { Icon(Icons.Default.DateRange, "Bookings") },
                label = { Text("Bookings") })
            NavigationBarItem(selected = selectedTabIndex == 2,
                onClick = { selectedTabIndex = 2 },
                icon = { Icon(Icons.Default.Email, "Complaints") },
                label = { Text("Complaints") })
        }
    }, floatingActionButton = {
        if (selectedTabIndex == 0) {
            FloatingActionButton(onClick = { showAddRoomDialog = true }) {
                Icon(Icons.Default.Add, "Add Room")
            }
        }
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTabIndex) {
                0 -> {
                    // Rooms Management Tab
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
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp)

                            ) {items(state.rooms) { room ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Room Info
                                            Column {
                                                Text(
                                                    text = "Room ${room.number}",
                                                    style = MaterialTheme.typography.titleLarge
                                                )
                                                Text(
                                                    text = when(room.type) {
                                                        RoomType.STANDARD -> "Single Room"
                                                        RoomType.DELUXE -> "Deluxe Room"
                                                        RoomType.SUITE -> "Suite Room"
                                                    },
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                                Text(
                                                    text = "£%.2f/night".format(room.pricePerNight),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(top = 4.dp)
                                                )
                                            }
                                            // Status Chip
                                            ElevatedFilterChip(
                                                selected = true,
                                                onClick = { },
                                                label = { Text(room.status.name) },
                                                colors = FilterChipDefaults.elevatedFilterChipColors(
                                                    selectedContainerColor = when (room.status) {
                                                        RoomStatus.AVAILABLE -> MaterialTheme.colorScheme.primaryContainer
                                                        RoomStatus.OCCUPIED -> MaterialTheme.colorScheme.errorContainer
                                                        RoomStatus.CLEANING -> MaterialTheme.colorScheme.tertiaryContainer
                                                        RoomStatus.DO_NOT_DISTURB -> MaterialTheme.colorScheme.errorContainer
                                                    }
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Update Status Button
                                        Button(
                                            onClick = { selectedRoomForStatus = room },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Update Status")
                                        }
                                    }
                                }
                            }
//                                items(state.rooms) { room ->
//                                    RoomCard(
//                                        room = room,
//                                        onStatusChange = { selectedRoom = room },
//                                        isStaffView = true,
//                                    )
//                                }
                            }
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
                    // Bookings Management Tab
                    Text(
                        text = "Bookings Management",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                2 -> {
                    // Complaints Management Tab
                    Text(
                        text = "Complaints Management",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
    // Add Room Dialog
    if (showAddRoomDialog) {
        AddRoomDialog(onDismiss = { showAddRoomDialog = false }, onAddRoom = { room ->
            roomViewModel.addRoom(room)
        })
    }
    // Status Update Dialog
    selectedRoomForStatus?.let { room ->
        AlertDialog(
            onDismissRequest = { selectedRoomForStatus = null },
            title = { Text("Update Room ${room.number} Status") },
            text = {
                Column {
                    RoomStatus.entries.forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    roomViewModel.updateRoomStatus(room.id, status)
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = room.status == status, 
                                onClick = {
                                roomViewModel.updateRoomStatus(room.id, status)
                                selectedRoomForStatus = null
                            })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = status.name, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedRoomForStatus = null }) {
                    Text("Cancel")
                }
            })
    }
}
