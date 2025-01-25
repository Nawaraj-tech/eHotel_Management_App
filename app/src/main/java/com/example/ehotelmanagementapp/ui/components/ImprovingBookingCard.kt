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
import com.example.ehotelmanagementapp.model.RoomType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingCard12(
    booking: Booking,
    onCancel:(String) -> Unit,
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
            // Header with Booking ID and Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Booking #${booking.id.take(8)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
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
            // Room Details Section
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
                        )
                        Text(
                            text = when(booking.roomType) {
                                RoomType.STANDARD -> "Standard Room"
                                RoomType.DELUXE -> "Deluxe Room"
                                RoomType.SUITE -> "Suite Room"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Dates Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Check-in",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dateFormatter.format(booking.checkInDate),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Check-out",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dateFormatter.format(booking.checkOutDate),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Price Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Price:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "£%.2f".format(booking.totalPrice),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            // Action Buttons
            if (booking.status == BookingStatus.PENDING) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Cancel Button
                    Button(
                        onClick =
                        { showCancelDialog = true },
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
    // Cancel Dialog (existing code remains the same)
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