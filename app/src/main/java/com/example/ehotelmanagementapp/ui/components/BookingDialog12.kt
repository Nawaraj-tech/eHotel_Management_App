package com.example.ehotelmanagementapp.ui.components

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ehotelmanagementapp.model.Room
import com.example.ehotelmanagementapp.model.RoomType
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDialog12(
    room: Room,
    userName: String,
    onDismiss: () -> Unit,
    onBookingComplete: () -> Unit, // Add to callback
    onBookRoom: (checkIn: Date, checkOut: Date, totalPrice: Double) -> Unit
) {
    var checkInDate by remember { mutableStateOf(Date()) }
    var checkOutDate by remember { mutableStateOf(Date(System.currentTimeMillis() + 86400000)) }
    var checkInError by remember { mutableStateOf<String?>(null) }
    var checkOutError by remember { mutableStateOf<String?>(null) }
    var     showSuccessDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    // Handle success navigation with delay

    // Validate dates
    fun validateDates(): Boolean {
        var isValid = true
        // Check if check-in date is in the past
        if (checkInDate.before(Date())) {
            checkInError = "Check-in date cannot be in the past"
            isValid = false
        } else {
            checkInError = null
        }
        // Check if check-out date is before check-in date
        if (checkOutDate.before(checkInDate)) {
            checkOutError = "Check-out date must be after check-in date"
            isValid = false
        } else if (checkOutDate == checkInDate) {
            checkOutError = "Check-out date must be at least one day after check-in"
            isValid = false
        } else {
            checkOutError = null
        }
        return isValid
    }
    if (!showSuccessDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Book Room ${room.number}",
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Guest Name: $userName".replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.ROOT
                            ) else it.toString()
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    //Spacer(modifier = Modifier.height(8.dp))
                    // Room Type Display
                    Text(
                        text = when (room.type) {
                            RoomType.STANDARD -> "Room Type: Standard Room"
                            RoomType.DELUXE -> "Room Type: Deluxe Room"
                            RoomType.SUITE -> "Room Type: Suite Room"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    //Spacer(modifier = Modifier.height(5.dp))
                    // Check-in Date
                    Text(
                        "Check-in Date",
                        style = MaterialTheme.typography.labelMedium
                    )
                    OutlinedTextField(
                        value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                            checkInDate
                        ),
                        onValueChange = { },
                        readOnly = true,
                        isError = checkInError != null,
                        trailingIcon = {
                            IconButton(onClick = {
                                showDatePicker(context, checkInDate) { newDate ->
                                    checkInDate = newDate
                                    validateDates()
                                }
                            }) {
                                Icon(Icons.Default.DateRange, "Select Check-in Date")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    checkInError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // Check-out Date
                    Text("Check-out Date", style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(checkOutDate),
                        onValueChange = { },
                        readOnly = true,
                        isError = checkOutError != null,
                        trailingIcon = {
                            IconButton(onClick = {
                                showDatePicker(context, checkOutDate) { newDate ->
                                    checkOutDate = newDate
                                    validateDates()
                                }
                            }) {
                                Icon(Icons.Default.DateRange, "Select Check-out Date")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    checkOutError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Price Summary
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Price Summary",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Price per night:")
                                Text("£%.2f".format(room.pricePerNight))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Number of nights:")
                                Text("${(checkOutDate.time - checkInDate.time) / (1000 * 60 * 60 * 24)}")
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Total:",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "£%.2f".format(room.pricePerNight * ((checkOutDate.time - checkInDate.time) / (1000 * 60 * 60 * 24))),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (validateDates()) {
                            val totalPrice = room.pricePerNight *
                                    ((checkOutDate.time - checkInDate.time) / (1000 * 60 * 60 * 24))
                            onBookRoom(checkInDate, checkOutDate, totalPrice)
                            showSuccessDialog = true
                        }
                    }
                ) {
                    Text("Confirm Booking")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
    if(showSuccessDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "Success",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Booking Successful!")
                }
            },
            text = {
                Text(
                    "Your room has been successfully booked. You will be redirected to your bookings.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDismiss()
                        onBookingComplete()
                    }
                   // Call this to handle navigation
                ) {
                    Text("View My Bookings")
                }
            }
        )

    }


}

private fun showDatePicker(
    context: Context,
    initialDate: Date,
    onDateSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance()
    calendar.time = initialDate
    DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day)
            onDateSelected(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}