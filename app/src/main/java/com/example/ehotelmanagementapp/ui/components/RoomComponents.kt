package com.example.ehotelmanagementapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ehotelmanagementapp.model.Room
import com.example.ehotelmanagementapp.model.RoomStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomCard(
    room: Room,
    onStatusChange: ((RoomStatus) -> Unit)? = null,
    onBookingClick: (() -> Unit)? = null,
    isStaffView: Boolean = false
) {
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
            Text(
                text = "Room ${room.number}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = room.type.name,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Status: ${room.status.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (room.status) {
                        RoomStatus.AVAILABLE -> MaterialTheme.colorScheme.primary
                        RoomStatus.OCCUPIED -> MaterialTheme.colorScheme.error
                        RoomStatus.CLEANING -> MaterialTheme.colorScheme.tertiary
                        RoomStatus.DO_NOT_DISTURB -> MaterialTheme.colorScheme.error
                    }
                )

                Text(
                    text = "£${room.pricePerNight}/night",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isStaffView && onStatusChange != null) {
                var showStatusMenu by remember { mutableStateOf(false) }

                Button(
                    onClick = { showStatusMenu = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Status")
                }

                DropdownMenu(
                    expanded = showStatusMenu,
                    onDismissRequest = { showStatusMenu = false }
                ) {
                    RoomStatus.values().forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.name) },
                            onClick = {
                                onStatusChange(status)
                                showStatusMenu = false
                            }
                        )
                    }
                }
            } else if (onBookingClick != null && room.status == RoomStatus.AVAILABLE) {
                Button(
                    onClick = onBookingClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Book Now")
                }
            }
        }
    }
}

@Composable
fun RoomsList(
    rooms: List<Room>,
    onStatusChange: ((String, RoomStatus) -> Unit)? = null,
    onBookingClick: ((String) -> Unit)? = null,
    isStaffView: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        rooms.forEach { room ->
            RoomCard(
                room = room,
                onStatusChange = { status -> onStatusChange?.invoke(room.id, status) },
                onBookingClick = { onBookingClick?.invoke(room.id) },
                isStaffView = isStaffView
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}