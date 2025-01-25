package com.example.ehotelmanagementapp.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ehotelmanagementapp.ui.components.ComplaintCard
import com.example.ehotelmanagementapp.ui.components.ComplaintDialog
import com.example.ehotelmanagementapp.viewmodel.BookingState
import com.example.ehotelmanagementapp.viewmodel.BookingViewModel
import com.example.ehotelmanagementapp.viewmodel.ComplaintViewModel
import com.example.ehotelmanagementapp.viewmodel.ComplaintsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerComplaintsScreen(
    navController: NavController,
    viewModel: ComplaintViewModel = hiltViewModel(),
    bookingViewModel: BookingViewModel = hiltViewModel()
) {
    var showAddComplaintDialog by remember { mutableStateOf(false) }
    val complaintsState by viewModel.complaintsState.collectAsState()
    val bookingsState by bookingViewModel.bookingState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadComplaints()
        bookingViewModel.loadBookings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Complaints") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (bookingsState is BookingState.Success && (bookingsState as BookingState.Success).bookings.isNotEmpty()) {
                FloatingActionButton(onClick = { showAddComplaintDialog = true }) {
                    Icon(Icons.Default.Add, "Add Complaint")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = complaintsState) {
                is ComplaintsState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ComplaintsState.Success -> {
                    if (state.complaints.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No complaints yet",
                                style = MaterialTheme.typography.titleLarge
                            )
                            if (bookingsState is BookingState.Success && (bookingsState as BookingState.Success).bookings.isNotEmpty()) {
                                Text(
                                    text = "Tap + to submit a complaint",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.complaints) { complaint ->
                                ComplaintCard(
                                    complaint = complaint,
                                    isStaff = false
                                )
                            }
                        }
                    }
                }
                is ComplaintsState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    if (showAddComplaintDialog && bookingsState is BookingState.Success) {
        ComplaintDialog(
            bookings = (bookingsState as BookingState.Success).bookings,
            onDismiss = { showAddComplaintDialog = false },
            onSubmit = { booking, title, description ->
                viewModel.submitComplaint(
                    booking.id,
                    booking.roomId,
                    booking.roomNumber,
                    title,
                    description
                )
                showAddComplaintDialog = false
            }
        )
    }
}


