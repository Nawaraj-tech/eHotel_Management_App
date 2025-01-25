package com.example.ehotelmanagementapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ehotelmanagementapp.model.BookingStatus
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.Timestamp
import javax.inject.Inject

// app/src/main/java/com/example/ehotelmanager/viewmodel/PaymentViewModel.kt
@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    fun processPayment(
        bookingId: String,
        amount: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val updates = hashMapOf<String, Any>(
                    "status" to BookingStatus.PAID.name,
                    "paidAmount" to amount,
                    "paidAt" to System.currentTimeMillis()
                )

                firestore.collection("bookings")
                    .document(bookingId)
                    .update(updates)
                    .await()

                onSuccess()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Payment failed")
            }
        }
    }
}
