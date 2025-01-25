package com.example.ehotelmanagementapp.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ehotelmanagementapp.model.Booking
import com.example.ehotelmanagementapp.model.BookingStatus
import com.example.ehotelmanagementapp.model.RoomType
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun SuccessOverlay(
    roomNumber: String,
    roomType: RoomType,
    checkInDate: Date,
    checkOutDate: Date,
    totalPrice: Double
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(32.dp)
                .width(320.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success Icon with Animation
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Success",
                    modifier = Modifier
                        .size(72.dp)
                        .padding(bottom = 16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                // Success Message
                Text(
                    text = "Booking Successful!",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(24.dp))
                // Booking Details
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Room Details
                        Text(
                            text = "Room ${roomNumber}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = when(roomType) {
                                RoomType.STANDARD -> "Standard Room"
                                RoomType.DELUXE -> "Deluxe Room"
                                RoomType.SUITE -> "Suite Room"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        // Dates
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Check-in",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = dateFormatter.format(checkInDate),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Check-out",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = dateFormatter.format(checkOutDate),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        // Total Price
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Price:",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "£%.2f".format(totalPrice),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Redirecting to your bookings...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}