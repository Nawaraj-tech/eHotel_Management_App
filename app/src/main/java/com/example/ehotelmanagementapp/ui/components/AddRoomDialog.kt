package com.example.ehotelmanagementapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ehotelmanagementapp.model.Room
import com.example.ehotelmanagementapp.model.RoomType
import com.example.ehotelmanagementapp.model.RoomStatus
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
                // Room Type
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { }
                ) {
                    OutlinedTextField(
                        value = selectedType.name,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Room Type") },
                        modifier = Modifier.menuAnchor()
                    )
                    DropdownMenu(
                        expanded = false,
                        onDismissRequest = { }
                    ) {
                        RoomType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name) },
                                onClick = { selectedType = type }
                            )
                        }
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