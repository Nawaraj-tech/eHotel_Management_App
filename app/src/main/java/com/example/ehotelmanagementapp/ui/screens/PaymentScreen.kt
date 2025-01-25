package com.example.ehotelmanagementapp.ui.screens

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ehotelmanagementapp.navigation.Screen
import com.example.ehotelmanagementapp.viewmodel.PaymentViewModel

// app/src/main/java/com/example/ehotelmanager/ui/screens/PaymentScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    bookingId: String,
    amount: Double,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    var cardNumber by remember { mutableStateOf("") }
    var cardHolderName by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errors by remember { mutableStateOf(mapOf<String, String>()) }

    fun formatCardNumber(input: String): String {
        val digitsOnly = input.filter { it.isDigit() }
        return digitsOnly.chunked(4).joinToString(" ").take(19)
    }

    fun formatExpiryDate(input: String): String {
        val digitsOnly = input.filter { it.isDigit() }
        return if (digitsOnly.length > 2) {
            "${digitsOnly.take(2)}/${digitsOnly.drop(2).take(2)}"
        } else {
            digitsOnly
        }
    }

    fun validateInputs(): Boolean {
        val newErrors = mutableMapOf<String, String>()

        if (cardNumber.replace(" ", "").length != 16) {
            newErrors["cardNumber"] = "Card number must be 16 digits"
        }

        if (cardHolderName.isBlank()) {
            newErrors["cardHolderName"] = "Card holder name is required"
        }

        if (!expiryDate.matches(Regex("^(0[1-9]|1[0-2])/([0-9]{2})\$"))) {
            newErrors["expiryDate"] = "Invalid expiry date (MM/YY)"
        }

        if (cvv.length != 3) {
            newErrors["cvv"] = "CVV must be 3 digits"
        }

        errors = newErrors
        return newErrors.isEmpty()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Amount Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Amount to Pay",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "£%.2f".format(amount),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Card Details
            OutlinedTextField(
                value = cardNumber,
                onValueChange = {
                    if (it.length <= 19) { // 16 digits + 3 spaces
                        cardNumber = formatCardNumber(it)
                    }
                },
                label = { Text("Card Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = errors.containsKey("cardNumber"),
                supportingText = {
                    errors["cardNumber"]?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = cardHolderName,
                onValueChange = { cardHolderName = it },
                label = { Text("Card Holder Name") },
                isError = errors.containsKey("cardHolderName"),
                supportingText = {
                    errors["cardHolderName"]?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = {
                        if (it.length <= 5) {
                            expiryDate = formatExpiryDate(it)
                        }
                    },
                    label = { Text("MM/YY") },
                    isError = errors.containsKey("expiryDate"),
                    supportingText = {
                        errors["expiryDate"]?.let { Text(it) }
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = cvv,
                    onValueChange = {
                        if (it.length <= 3 && it.all { c -> c.isDigit() }) {
                            cvv = it
                        }
                    },
                    label = { Text("CVV") },
                    isError = errors.containsKey("cvv"),
                    supportingText = {
                        errors["cvv"]?.let { Text(it) }
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (validateInputs()) {
                        viewModel.processPayment(
                            bookingId = bookingId,
                            amount = amount,
                            onSuccess = { showSuccessDialog = true },
                            onError = { message ->
                                // Handle error
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Pay £%.2f".format(amount))
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "Success",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Payment Successful!")
                }
            },
            text = {
                Text(
                    "Your payment has been processed successfully.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        navController.navigate(Screen.CustomerHome.route) {
                            popUpTo(Screen.CustomerHome.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View My Bookings")
                }
            }
        )
    }
}