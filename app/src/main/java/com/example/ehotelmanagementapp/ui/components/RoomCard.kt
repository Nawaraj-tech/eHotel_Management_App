package com.example.ehotelmanagementapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ehotelmanagementapp.model.Room
import com.example.ehotelmanagementapp.model.RoomStatus
import com.example.ehotelmanagementapp.model.RoomType
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomCard(
    room: Room,
    onStatusChange: ((RoomStatus) -> Unit)? = null,
    onBookRoom: ((Room) -> Unit)? = null,
    isStaffView: Boolean = false
) {
    var showStatusDialog by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Room ${room.number}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = when(room.type) {
                            RoomType.STANDARD -> "Standard Room"
                            RoomType.DELUXE -> "Deluxe Room"
                            RoomType.SUITE -> "Suite Room"
                        },
                        style = MaterialTheme.typography.bodyLarge
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
                Text(
                    text = "£%.2f/night".format(room.pricePerNight),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Status with color-coding
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                // Status with color-coding using FilterChip
//                ElevatedFilterChip(
//                    selected = true,
//                    onClick = { },
//                    label = {
//                        Text(
//                            when (room.status) {
//                                RoomStatus.AVAILABLE -> "Available"
//                                RoomStatus.OCCUPIED -> "Occupied"
//                                RoomStatus.CLEANING -> "Cleaning"
//                                RoomStatus.DO_NOT_DISTURB -> "Do Not Disturb"
//                            }
//                        )
//                    },
//                    colors = FilterChipDefaults.elevatedFilterChipColors(
//                        selectedContainerColor = when (room.status) {
//                            RoomStatus.AVAILABLE -> MaterialTheme.colorScheme.primaryContainer
//                            RoomStatus.OCCUPIED -> MaterialTheme.colorScheme.errorContainer
//                            RoomStatus.CLEANING -> MaterialTheme.colorScheme.tertiaryContainer
//                            RoomStatus.DO_NOT_DISTURB -> MaterialTheme.colorScheme.errorContainer
//                        },
//                        selectedLabelColor = when (room.status) {
//                            RoomStatus.AVAILABLE -> MaterialTheme.colorScheme.primaryContainer
//                            RoomStatus.OCCUPIED -> MaterialTheme.colorScheme.errorContainer
//                            RoomStatus.CLEANING -> MaterialTheme.colorScheme.tertiaryContainer
//                            RoomStatus.DO_NOT_DISTURB -> MaterialTheme.colorScheme.errorContainer
//                        }
//                    )
//                )
//            }

            // Action buttons
            if (isStaffView && onStatusChange != null) {
                Button(
                    onClick = { showStatusDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Status")
                }
            } else if (!isStaffView && room.status == RoomStatus.AVAILABLE && onBookRoom != null) {
                Button(
                    onClick = { onBookRoom(room) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Book Now")
                }
            }
        }
    }
    // Status Update Dialog for Staff
    if (showStatusDialog && onStatusChange != null) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Update Room Status") },
            text = {
                Column {
                    RoomStatus.values().forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = room.status == status,
                                onClick = {
                                    onStatusChange(status)
                                    showStatusDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (status) {
                                    RoomStatus.AVAILABLE -> "Available"
                                    RoomStatus.OCCUPIED -> "Occupied"
                                    RoomStatus.CLEANING -> "Cleaning"
                                    RoomStatus.DO_NOT_DISTURB -> "Do Not Disturb"
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}