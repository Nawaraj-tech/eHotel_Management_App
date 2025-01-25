package com.example.ehotelmanagementapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ehotelmanagementapp.model.Booking
import com.example.ehotelmanagementapp.viewmodel.ComplaintViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintDialog(
    viewModel: ComplaintViewModel = hiltViewModel(),
    bookings: List<Booking>,
    onDismiss: () -> Unit,
    onSubmit: (Booking, String, String) -> Unit
) {
    var selectedBooking by remember { mutableStateOf<Booking?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var expandedDropDown by remember { mutableStateOf(false) }
    var errors by remember { mutableStateOf(mapOf<String, String>()) }




    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Submit Complaint",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Room Selection
                ExposedDropdownMenuBox(
                    expanded =  expandedDropDown,
                    onExpandedChange = {  expandedDropDown = it }
                ) {
                    OutlinedTextField(
                        value = selectedBooking?.roomNumber?.let { "Room $it" } ?: "Select Room",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Room Number") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded =  expandedDropDown)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedDropDown,
                        onDismissRequest = {  expandedDropDown = false }
                    ) {
                        bookings.forEach { booking ->
                            DropdownMenuItem(
                                text = { Text("Room ${booking.roomNumber}") },
                                onClick = {
                                    selectedBooking = booking
                                    expandedDropDown = false
                                }
                            )
                        }
                    }
                }

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    isError = errors.containsKey("title"),
                    supportingText = { errors["title"]?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    isError = errors.containsKey("description"),
                    supportingText = { errors["description"]?.let { Text(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newErrors = mutableMapOf<String, String>()
                    if (selectedBooking == null) newErrors["room"] = "Please select a room"
                    if (title.isBlank()) newErrors["title"] = "Title is required"
                    if (description.isBlank()) newErrors["description"] = "Description is required"

                    if (newErrors.isEmpty()) {
                        selectedBooking?.let { booking ->
                            onSubmit(booking, title, description)
                        }
                    } else {
                        errors = newErrors
                    }
                }
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}