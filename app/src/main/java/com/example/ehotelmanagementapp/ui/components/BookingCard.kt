/*
package com.example.ehotelmanagementapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ehotelmanagementapp.model.Booking
import com.example.ehotelmanagementapp.model.BookingStatus
import java.text.SimpleDateFormat
import java.util.*

*/
/*@Composable
fun BookingCard(
    booking: Booking,
    onCancel: (String) -> Unit,
    onPayNow: (String,Double) -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var showCancelDialog by remember { mutableStateOf(false) }
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
            // Guest Name and Booking Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.guestName,
                    style = MaterialTheme.typography.titleLarge
                )
                ElevatedFilterChip(
                    selected = true,
                    onClick = { },
                    label = { Text(booking.status.name) },
                    colors = FilterChipDefaults.elevatedFilterChipColors(
                        selectedContainerColor = when (booking.status) {
                            BookingStatus.CONFIRMED -> MaterialTheme.colorScheme.primaryContainer
                            BookingStatus.PENDING -> MaterialTheme.colorScheme.tertiaryContainer
                            BookingStatus.CHECKED_IN -> MaterialTheme.colorScheme.secondaryContainer
                            BookingStatus.CHECKED_OUT -> MaterialTheme.colorScheme.surfaceVariant
                            BookingStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer

                            else -> Materialtheme.colorScheme.surfaceVariant
                        },
                        selectedLabelColor = when (booking.status) {
                            BookingStatus.CONFIRMED -> MaterialTheme.colorScheme.primaryContainer
                            BookingStatus.PENDING -> MaterialTheme.colorScheme.tertiaryContainer
                            BookingStatus.CHECKED_IN -> MaterialTheme.colorScheme.secondaryContainer
                            BookingStatus.CHECKED_OUT -> MaterialTheme.colorScheme.surfaceVariant
                            BookingStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
                        }
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Check-in",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = dateFormatter.format(booking.checkInDate),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Check-out",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = dateFormatter.format(booking.checkOutDate),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Total Price
            Text(
                text = "Total Price: £%.2f".format(booking.totalPrice),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.End)
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Cancel Button - Only show for pending or confirmed bookings
            if (booking.status in listOf(BookingStatus.PENDING, BookingStatus.CONFIRMED)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top=16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showCancelDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel Booking")
                    }
                    Button(
                        onClick = {onPayNow(booking.id,booking.totalPrice)},
                        modifier  = Modifier.weight(1f)
                    ){
                        Text("Pay Now")

                    }
                }

            }
        }
    }
    // Cancel Confirmation Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Booking") },
            text = { Text("Are you sure you want to cancel this booking?") },
            confirmButton = {
                Button(
                    onClick = {
                        onCancel(booking.id)
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Yes, Cancel")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("No, Keep Booking")
                }
            }
        )
    }
}*//*

@Composable
fun BookingCard(
    booking: Booking,
    onCancel: (String) -> Unit,
    onPayNow: (String, Double) -> Unit
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
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
            // Booking Details Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Booking #${booking.id.take(8)}",
                        style = MaterialTheme.typography.titleMedium
                    )

                }
                // Status Chip
                ElevatedFilterChip(
                    selected = true,
                    onClick = { },
                    label = { Text(booking.status.name) },
                    colors = FilterChipDefaults.elevatedFilterChipColors(
                        selectedContainerColor = when (booking.status) {
                            BookingStatus.PENDING -> MaterialTheme.colorScheme.tertiaryContainer
                            BookingStatus.CONFIRMED -> MaterialTheme.colorScheme.primaryContainer
                            BookingStatus.PAID -> MaterialTheme.colorScheme.secondaryContainer
                            BookingStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                )
            }
            Spacer(modifier = Modifier.height(12.dp))


            // Room Detials Section

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                            Text(
                                text = "Room ${booking.roomNumber}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            Text(
                                text = "Check-in: ${dateFormatter.format(booking.checkInDate)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Check-out: ${dateFormatter.format(booking.checkOutDate)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                    }
                        Text(
                            text = "£%.2f".format(booking.totalPrice),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

            }
            // Dates and Price

            Spacer(modifier = Modifier.height(16.dp))
            // Action Buttons
            if (booking.status == BookingStatus.PENDING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Cancel Button
                    Button(
                        onClick = { showCancelDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    // Pay Now Button
                    Button(
                        onClick = { onPayNow(booking.id, booking.totalPrice) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pay Now")
                    }
                }
            }
        }
    }
    // Cancel Confirmation Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Booking") },
            text = { Text("Are you sure you want to cancel this booking?") },
            confirmButton = {
                Button(
                    onClick = {
                        onCancel(booking.id)
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Yes, Cancel")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("No, Keep Booking")
                }
            }
        )
    }
}



*/
