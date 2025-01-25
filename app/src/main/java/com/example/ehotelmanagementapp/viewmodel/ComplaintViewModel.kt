package com.example.ehotelmanagementapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ehotelmanagementapp.model.Booking
import com.example.ehotelmanagementapp.model.BookingStatus
import com.example.ehotelmanagementapp.model.Complaint
import com.example.ehotelmanagementapp.model.ComplaintStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


// ComplaintViewModel.kt
@HiltViewModel
class ComplaintViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _complaintsState = MutableStateFlow<ComplaintsState>(ComplaintsState.Loading)
    val complaintsState: StateFlow<ComplaintsState> = _complaintsState

    init {
        loadComplaints()
    }

    fun loadComplaints() {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser
                if (currentUser == null) {
                    _complaintsState.value = ComplaintsState.Error("User not authenticated")
                    return@launch
                }

                val userDoc = firestore.collection("users")
                    .document(currentUser.uid)
                    .get()
                    .await()

                val isStaff = userDoc.getString("role") == "STAFF"

                val query = if (isStaff) {
                    firestore.collection("complaints")
                } else {
                    firestore.collection("complaints")
                        .whereEqualTo("userId", currentUser.uid)
                }

                val complaints = query.get().await()
                    .documents
                    .mapNotNull { doc ->
                        doc.toObject(Complaint::class.java)?.copy(id = doc.id)
                    }

                _complaintsState.value = ComplaintsState.Success(complaints)
            } catch (e: Exception) {
                _complaintsState.value =
                    ComplaintsState.Error(e.message ?: "Failed to load complaints")
            }
        }
    }

    // Update ComplaintViewModel
    private suspend fun validateBookingForComplaint(bookingId: String): Boolean {
        return viewModelScope.async {
            try {
                val booking = firestore.collection("bookings")
                    .document(bookingId)
                    .get()
                    .await()
                    .toObject(Booking::class.java)

                booking?.status == BookingStatus.PAID || booking?.status == BookingStatus.CONFIRMED
            } catch (e: Exception) {
                false
            }
        }.await()
    }


    fun submitComplaint(
        bookingId: String,
        roomId: String, roomNumber: String, title: String, description: Any?
    ) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: throw Exception("Not authenticated")

                // Verfiy user has valid Booking
                /* if(!validateBookingForComplaint(bookingId)){
                     throw Exception(" Only user with confirmed or paid booking can submit complaints")
                 }*/
                // Check if the booking exists and is paid
                val bookingDoc = firestore.collection("bookings")
                    .document(bookingId)
                    .get()
                    .await()
                val booking = bookingDoc.toObject(Booking::class.java)

                if (booking?.status == BookingStatus.PAID || booking?.status == BookingStatus.CONFIRMED) {
                    val complaint = Complaint(
                        userId = currentUser.uid,
                        bookingId = bookingId,
                        roomId = roomId,
                        roomNumber = roomNumber,
                        title = title,
                        description = description
                    )
                    firestore.collection("complaints")
                        .add(complaint)
                        .await()
                    loadComplaints()
                } else {
                    throw Exception("Only users with confirmed or paid bookings can submit complaints")
                }


            } catch (e: Exception) {
                _complaintsState.value =
                    ComplaintsState.Error(e.message ?: "Failed to submit complaint")
            }
        }
    }

    fun updateComplaintStatus(complaintId: String, status: ComplaintStatus) {
        viewModelScope.launch {
            try {
                val updates = if (status == ComplaintStatus.RESOLVED) {
                    hashMapOf(
                        "status" to status.name,
                        "resolveAt" to System.currentTimeMillis()
                    )
                } else {
                    hashMapOf(
                        "status" to status.name,
                        "resolveAt" to null
                    ) as HashMap<String, Any>
                }

                firestore.collection("complaints")
                    .document(complaintId)
                    .update(updates)
                    .await()

                loadComplaints()
            } catch (e: Exception) {
                _complaintsState.value =
                    ComplaintsState.Error(e.message ?: "Failed to update complaint")
            }
        }
    }
}

sealed class ComplaintsState {
    object Loading : ComplaintsState()
    data class Success(val complaints: List<Complaint>) : ComplaintsState()
    data class Error(val message: String) : ComplaintsState()
}