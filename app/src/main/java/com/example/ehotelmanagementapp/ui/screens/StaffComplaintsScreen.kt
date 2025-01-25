package com.example.ehotelmanagementapp.ui.screens
import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ehotelmanagementapp.model.ComplaintStatus
import com.example.ehotelmanagementapp.model.Room
import com.example.ehotelmanagementapp.model.RoomStatus
import com.example.ehotelmanagementapp.navigation.Screen
import com.example.ehotelmanagementapp.ui.components.BookingCard12
import com.example.ehotelmanagementapp.ui.components.BookingDialog12
import com.example.ehotelmanagementapp.ui.components.ComplaintCard
import com.example.ehotelmanagementapp.ui.components.RoomCard
import com.example.ehotelmanagementapp.viewmodel.AuthViewModel
import com.example.ehotelmanagementapp.viewmodel.BookingState
import com.example.ehotelmanagementapp.viewmodel.BookingViewModel
import com.example.ehotelmanagementapp.viewmodel.ComplaintViewModel
import com.example.ehotelmanagementapp.viewmodel.ComplaintsState
import com.example.ehotelmanagementapp.viewmodel.RoomState
import com.example.ehotelmanagementapp.viewmodel.RoomViewModel
import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffComplaintsScreen(
    navController: NavController,
    viewModel: ComplaintViewModel = hiltViewModel()
) {
    val complaintsState by viewModel.complaintsState.collectAsState()
    var selectedStatus by remember { mutableStateOf<ComplaintStatus?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complaints Management") },
                actions = {
                    // Filter complaints by status
                    IconButton(onClick = { selectedStatus = null }) {
                        Icon(Icons.Default.Search, "Filter")
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
            // Status Filter Chips
            ScrollableRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                ComplaintStatus.values().forEach { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = { selectedStatus = if (selectedStatus == status) null else status },
                        label = { Text(status.name) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            when (val state = complaintsState) {
                is ComplaintsState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ComplaintsState.Success -> {
                    val filteredComplaints = if (selectedStatus != null) {
                        state.complaints.filter { it.status == selectedStatus }
                    } else {
                        state.complaints
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(filteredComplaints) { complaint ->
                            ComplaintCard(
                                complaint = complaint,
                                isStaff = true,
                                onStatusUpdate = { newStatus ->
                                    viewModel.updateComplaintStatus(complaint.id, newStatus)
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                is ComplaintsState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ScrollableRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content()
    }
}