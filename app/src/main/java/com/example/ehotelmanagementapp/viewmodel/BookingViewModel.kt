package com.example.ehotelmanagementapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ehotelmanagementapp.model.Booking
import com.example.ehotelmanagementapp.model.BookingStatus
import com.example.ehotelmanagementapp.model.RoomStatus
import com.example.ehotelmanagementapp.model.RoomType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

sealed class BookingState {
    object Loading : BookingState()
    data class Success(val bookings: List<Booking>) : BookingState()
    data class Error(val message: String) : BookingState()
}

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _bookingState = MutableStateFlow<BookingState>(BookingState.Loading)
    val bookingState: StateFlow<BookingState> = _bookingState
    fun loadBookings() {
        viewModelScope.launch {
            try {
                _bookingState.value = BookingState.Loading
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val bookingSnapshot = firestore
                        .collection("bookings")
                        .whereEqualTo("userId", currentUser.uid)
                        .get()
                        .await()
                    val bookings = bookingSnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Booking::class.java)?.copy(id = doc.id)
                    }
                    _bookingState.value = BookingState.Success(bookings)
                }

            } catch (e: Exception) {
                Log.e("BookingViewModel", "Error loading bookings", e)
                _bookingState.value = BookingState.Error(e.message ?: "Failed to load bookings")
            }
        }
    }

    fun createBooking(
        roomId: String,
        roomNumber: String,
        roomType: RoomType,
        checkInDate: Date,
        checkOutDate: Date,
        guestName: String,
        totalPrice: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser
                if (currentUser == null) {
                    onError("User not authenticated")
                    return@launch
                }
                val booking = Booking(
                    userId = currentUser.uid,
                    roomId = roomId,
                    roomNumber = roomNumber,
                    roomType = roomType,
                    checkInDate = checkInDate,
                    checkOutDate = checkOutDate,
                    totalPrice = totalPrice,
                    status = BookingStatus.PENDING,
                    guestName = guestName,
                    createdAt = Date()
                )
                // Create booking
                firestore.collection("bookings")
                    .add(booking)
                    .await()
                // Update room status
                firestore.collection("rooms")
                    .document(roomId)
                    .update("status", RoomStatus.OCCUPIED)
                    .await()
                onSuccess()
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Error creating booking", e)
                onError(e.message ?: "Failed to create booking")
            }
        }
    }

    fun updateBookingStatus(
        bookingId: String,
        status: BookingStatus,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                firestore.collection("bookings")
                    .document(bookingId)
                    .update("status", status)
                    .await()
                loadBookings()
                onSuccess()
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Error updating booking", e)
                onError(e.message ?: "Failed to update booking")
            }
        }
    }

    fun cancelBooking(
        bookingId: String,
        roomId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Update booking status
                firestore.collection("bookings")
                    .document(bookingId)
                    .update("status", BookingStatus.CANCELLED)
                    .await()
                // Update room status
                firestore.collection("rooms")
                    .document(roomId)
                    .update("status", RoomStatus.AVAILABLE)
                    .await()
                loadBookings()
                onSuccess()
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Error cancelling booking", e)
                onError(e.message ?: "Failed to cancel booking")
            }
        }
    }
}

