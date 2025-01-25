package com.example.ehotelmanagementapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MailOutline
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ehotelmanagementapp.model.Room
import com.example.ehotelmanagementapp.model.RoomStatus
import com.example.ehotelmanagementapp.navigation.Screen
import com.example.ehotelmanagementapp.ui.components.BookingCard12
import com.example.ehotelmanagementapp.ui.components.BookingDialog12
import com.example.ehotelmanagementapp.ui.components.RoomCard
import com.example.ehotelmanagementapp.viewmodel.AuthViewModel
import com.example.ehotelmanagementapp.viewmodel.BookingState
import com.example.ehotelmanagementapp.viewmodel.BookingViewModel
import com.example.ehotelmanagementapp.viewmodel.RoomState
import com.example.ehotelmanagementapp.viewmodel.RoomViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    navController: NavController,
    roomViewModel: RoomViewModel = hiltViewModel(),
    bookingViewModel: BookingViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val roomsState by roomViewModel.roomsState.collectAsState()
    val bookingState by bookingViewModel.bookingState.collectAsState()
    var selectedRoom by remember { mutableStateOf<Room?>(null) }
    val userState by authViewModel.currentUser.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }
    var isBlurred by remember { mutableStateOf(false) }

    var showBookingError by remember { mutableStateOf<String?>(null) }
    // Show snackbar for messages
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Effect to load bookings when tab changes or after new booking

    LaunchedEffect(selectedTabIndex, showSuccessDialog) {
        if (selectedTabIndex == 1) {
            bookingViewModel.loadBookings()
        }
    }

    // Fetch the user name when screen if first displayed

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(topBar = {
            TopAppBar(title = { Text("Customer Dashboard") }, actions = {
                IconButton(onClick = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }) {
                    Icon(Icons.Filled.ExitToApp, "Logout")
                }
            })
        }, bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    icon = { Icon(Icons.Default.Home, "Available Rooms") },
                    label = { Text("Rooms") })
                NavigationBarItem(selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    icon = { Icon(Icons.Default.DateRange, "My Bookings") },
                    label = { Text("My Bookings") })
                NavigationBarItem(
                    selected = selectedTabIndex == 2,
                    onClick = {
                        selectedTabIndex = 2
                        navController.navigate(Screen.CustomerComplaints.route)
                    },
                    icon = { Icon(Icons.Default.MailOutline, "Complaints") },
                    label = { Text("Complaints") }
                )
            }
        }) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .blur(if (isBlurred) 8.dp else 0.dp)
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
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(availableRooms) { room ->
                                        RoomCard(room = room, onBookRoom = { selectedRoom = it })
                                    }
                                }
                            }

                            is RoomState.Error -> {
                                Text(
                                    text = state.message,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                            else -> {}
                        }
                    }

                    1 -> {
                        // My Bookings Tab
                        when (val state = bookingState) {
                            is BookingState.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            is BookingState.Success -> {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(state.bookings) { booking ->
                                        BookingCard12(booking = booking, onCancel = { bookingId ->
                                            bookingViewModel.cancelBooking(bookingId = bookingId,
                                                roomId = booking.roomId,
                                                onSuccess = {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("Booking cancelled successfully")
                                                    }
                                                },
                                                onError = { error ->
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(error)
                                                    }
                                                })
                                        }, onPayNow = { bookingId, amount ->
                                            navController.navigate(
                                                "payment/$bookingId/$amount"
                                            )
                                        })
                                    }
                                }
                            }

                            is BookingState.Error -> {
                                Text(
                                    text = state.message,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(onDismissRequest = {
            showSuccessDialog = false
            selectedTabIndex = 1 // Switch to My Bookings tab
        }, title = {
            Text("Booking Successful")
        }, text = {
            Text("Your booking has been successfully completed.")
        }, confirmButton = {
            Button(onClick = {
                showSuccessDialog = false
                selectedTabIndex = 1 // Switch to My Bookings tab
            }) {
                Text("View My Bookings")
            }
        })
    }

    // Show booking dialog when room is selected
    selectedRoom?.let { room ->
        userState?.let { user ->
            BookingDialog12(
                room = room,
                userName = user.name,
                onDismiss = { selectedRoom = null },
                onBookingComplete = {
                    selectedTabIndex =1 // Switch to booking tab
                    bookingViewModel.loadBookings() // Refresh Bookings
                },
                onBookRoom = { checkIn, checkout, totalPrice ->
                    bookingViewModel.createBooking(
                        roomId = room.id,
                        roomNumber = room.number,
                        roomType = room.type,
                        checkInDate = checkIn,
                        checkOutDate = checkout,
                        guestName = user.name,
                        totalPrice = totalPrice,
                        onSuccess = {
                            selectedRoom = null // close dialog
                            showSuccessDialog = true // show success Dialog
                            bookingViewModel.loadBookings() // Reload booking immediately
                            /*scope.launch {
                                snackbarHostState.showSnackbar("Booking created successfully")
                            }*/
                        },
                        onError = { error ->
                            Log.e("CustomerHomeScreen", "Booking error: $error")
                        }
                    )
                }
            )
        }

    }

}

