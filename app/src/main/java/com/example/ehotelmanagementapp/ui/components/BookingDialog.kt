package com.example.ehotelmanagementapp.ui.components

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ehotelmanagementapp.model.Room
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDialog(
    room: Room,
    userName: String,
    onDismiss: () -> Unit,
    onBookingComplete: ()-> Unit,
    onBookRoom: (checkIn: Date, checkOut: Date, totalPrice: Double) -> Unit
) {

    var checkInDate by remember { mutableStateOf(Date()) }
    var checkOutDate by remember { mutableStateOf(Date(System.currentTimeMillis() + 86400000)) } // Next day
    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var showSuccessOverlay by remember { mutableStateOf(false) }

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val context = LocalContext.current

    // Calculate total price
    val numberOfDays = max(1, (checkOutDate.time - checkInDate.time) / 86400000)
    val totalPrice = numberOfDays * room.pricePerNight

    // Handle validation

    LaunchedEffect(showSuccessOverlay) {
        if (showSuccessOverlay) {
            //onDismiss()
            onBookingComplete() // this will handle the both navigation and dismissal
            delay(50000)
        }
    }


    /* if (showSuccessDialog) {
         AlertDialog(
             onDismissRequest = {
                 showSuccessDialog = false
                 onDismiss()
             },
             title = { Text("Booking Successful") },
             text = { Text("You have successfully booked Room ${room.number}.Please proceed to payment") },
             confirmButton = {
                 Button(onClick = {
                     showSuccessDialog = false
                     onDismiss()
                 }) {
                     Text("Ok")
                 }
             }
         )
         return
     }*/
    Box(modifier = Modifier.fillMaxSize()) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Room ${room.number}") },
            text = {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .blur(if (showSuccessOverlay) 8.dp else 0.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Guest Name (ReadOnly)
                OutlinedTextField(
                    value = userName.ifBlank { "Loading user information....." },
                    onValueChange = { },
                    label = { Text("Guest Name") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    readOnly = true
                )
                // Check-in Date
                OutlinedTextField(value = dateFormatter.format(checkInDate),
                    onValueChange = { },
                    label = { Text("Check-in Date") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            val calendar = Calendar.getInstance()
                            calendar.time = checkInDate
                            DatePickerDialog(
                                context,
                                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                                    calendar.set(year, month, dayOfMonth)
                                    checkInDate = calendar.time
                                    // Ensure checkout is after checkin
                                    if (checkOutDate.before(checkInDate)) {
                                        errors =
                                            errors + ("checkIn" to "Check-in date cannot be in the past")
                                        checkOutDate = calendar.time
                                    } else {
                                        checkInDate = calendar.time
                                        errors = errors - "checkIn"
                                    }
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }) {
                            Icon(Icons.Default.DateRange, "Select Date")
                        }
                    })
                errors["checkIn"]?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                // Check-out Date
                OutlinedTextField(value = dateFormatter.format(checkOutDate),
                    onValueChange = { },
                    label = { Text("Check-out Date") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            val calendar = Calendar.getInstance()
                            calendar.time = checkOutDate
                            DatePickerDialog(
                                context,
                                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                                    calendar.set(year, month, dayOfMonth)
                                    if (calendar.time.after(checkInDate)) {
                                        errors =
                                            errors + ("Checkout" to "Check-out date must be after check in date")
                                    } else {
                                        checkOutDate = calendar.time
                                        errors = errors - "checkOut"
                                    }
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }) {
                            Icon(Icons.Default.DateRange, "Select Date")
                        }
                    })
                // Total Price
                Text(
                    text = "Total Price: £%.2f".format(totalPrice),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }, confirmButton = {
            Button(
                onClick = {
                    if (errors.isEmpty()) {
                        onBookRoom(checkInDate, checkOutDate, totalPrice)
                        showSuccessOverlay = true
                    }
                }, enabled = userName.isNotBlank() && errors.isEmpty()
            ) {
                Text("Confirm Booking")
            }

        }, dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")

            }
        }
        )
        // Success overlay
        if (showSuccessOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false) { },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .width(320.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )

                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Rounded.CheckCircle,
                            contentDescription = "Success",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Booking Successful!",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your room has been booked successfully.Redirecting to your bookings....",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        ) //
                        LinearProgressIndicator(
                            modifier = Modifier
                                .padding(top=24.dp)
                                .fillMaxWidth()
                        )

                    }

                }
            }
        }

    }

}




