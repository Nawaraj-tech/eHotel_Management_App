package com.example.ehotelmanagementapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ehotelmanagementapp.model.Room
import com.example.ehotelmanagementapp.model.RoomType
import com.example.ehotelmanagementapp.model.RoomStatus
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoomDialog(
    onDismiss: () -> Unit,
    onAddRoom: (Room) -> Unit
) {
    var roomNumber by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(RoomType.STANDARD) }
    var price by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isRoomTypeExpanded by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Room") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Room Number
                OutlinedTextField(
                    value = roomNumber,
                    onValueChange = { roomNumber = it },
                    label = { Text("Room Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                // Room Type Dropdown
                Box {
                    OutlinedTextField(
                        value = when(selectedType) {
                            RoomType.STANDARD -> "Standard Room"
                            RoomType.DELUXE -> "Deluxe Room"
                            RoomType.SUITE -> "Suite Room"
                        },
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Room Type") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(
                               imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Room Type",
                                modifier = Modifier.clickable {
                                    isRoomTypeExpanded = !isRoomTypeExpanded
                                }
                            )
                        }
                    )
                    DropdownMenu(
                        expanded = isRoomTypeExpanded,
                        onDismissRequest = { isRoomTypeExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Standard Room") },
                            onClick = {
                                selectedType = RoomType.STANDARD
                                isRoomTypeExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Deluxe Room") },
                            onClick = {
                                selectedType = RoomType.DELUXE
                                isRoomTypeExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Suite Room") },
                            onClick = {
                                selectedType = RoomType.SUITE
                                isRoomTypeExpanded = false
                            }
                        )
                    }
                }
                // Price
                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            price = it
                        }
                    },
                    label = { Text("Price per Night (£)") },
                    modifier = Modifier.fillMaxWidth()
                )
                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (roomNumber.isBlank()) {
                        error = "Room number is required"
                        return@Button
                    }
                    if (price.isBlank()) {
                        error = "Price is required"
                        return@Button
                    }
                    val newRoom = Room(
                        number = roomNumber,
                        type = selectedType,
                        status = RoomStatus.AVAILABLE,
                        pricePerNight = price.toDoubleOrNull() ?: 0.0
                    )
                    onAddRoom(newRoom)
                    onDismiss()
                }
            ) {
                Text("Add Room")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
