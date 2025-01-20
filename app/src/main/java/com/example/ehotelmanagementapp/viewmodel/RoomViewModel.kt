package com.example.ehotelmanagementapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ehotelmanagementapp.model.Room
import com.example.ehotelmanagementapp.model.RoomStatus
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class RoomState {
    object Loading : RoomState()
    data class Success(val rooms: List<Room>) : RoomState()
    data class Error(val message: String) : RoomState()
}

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _roomsState = MutableStateFlow<RoomState>(RoomState.Loading)
    val roomsState: StateFlow<RoomState> = _roomsState

    init {
        loadRooms()
    }

    fun loadRooms() {
        viewModelScope.launch {
            try {
                _roomsState.value = RoomState.Loading
                val roomsSnapshot = firestore.collection("rooms").get().await()
                val rooms = roomsSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Room::class.java)?.copy(id = doc.id)
                }
                _roomsState.value = RoomState.Success(rooms)
            } catch (e: Exception) {
                _roomsState.value = RoomState.Error(e.message ?: "Failed to load rooms")
            }
        }
    }


    fun updateRoomStatus(roomId: String, status: RoomStatus) {
        viewModelScope.launch {
            try {
                firestore.collection("rooms")
                    .document(roomId)
                    .update("status", status)
                    .await()
                loadRooms() // Reload rooms after update
            } catch (e: Exception) {
                _roomsState.value = RoomState.Error(e.message ?: "Failed to update room status")
            }
        }
    }

    fun addRoom(room: Room) {
        viewModelScope.launch {
            try {
                firestore.collection("rooms")
                    .add(room)
                    .await()
                loadRooms()
            } catch (e: Exception) {
                _roomsState.value = RoomState.Error(e.message ?: "Failed to add room")
            }
        }
    }
    fun deleteRoom(roomId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("rooms")
                    .document(roomId)
                    .delete()
                    .await()
                loadRooms()
            } catch (e: Exception) {
                Log.e("RoomViewModel", "Error deleting room", e)
                _roomsState.value = RoomState.Error(e.message ?: "Failed to delete room")
            }
        }
    }
}