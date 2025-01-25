package com.example.ehotelmanagementapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ehotelmanagementapp.model.Complaint
import com.example.ehotelmanagementapp.model.ComplaintStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun ComplaintCard(
    complaint: Complaint,
    isStaff: Boolean,
    onStatusUpdate: ((ComplaintStatus) -> Unit)? = null
) {
    var showStatusDialog by remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Room ${complaint.roomNumber}",
                    style = MaterialTheme.typography.titleMedium
                )

                ElevatedFilterChip(
                    selected = true,
                    onClick = { if (isStaff) showStatusDialog = true },
                    label = { Text(complaint.status.name) },
                    colors = FilterChipDefaults.elevatedFilterChipColors(
                        selectedContainerColor = when (complaint.status) {
                            ComplaintStatus.OPEN -> MaterialTheme.colorScheme.errorContainer
                            ComplaintStatus.IN_PROGRESS -> MaterialTheme.colorScheme.tertiaryContainer
                            ComplaintStatus.RESOLVED -> MaterialTheme.colorScheme.primaryContainer
                            ComplaintStatus.CLOSED -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = complaint.title,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Submitted: ${dateFormatter.format(Date(complaint.createdAt))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            complaint.resolvedAt?.let {
                Text(
                    text = "Resolved: ${dateFormatter.format(Date(it))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showStatusDialog && isStaff && onStatusUpdate != null) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Update Status") },
            text = {
                Column {
                    ComplaintStatus.values().forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onStatusUpdate(status)
                                    showStatusDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = complaint.status == status,
                                onClick = {
                                    onStatusUpdate(status)
                                    showStatusDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(status.name)
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


