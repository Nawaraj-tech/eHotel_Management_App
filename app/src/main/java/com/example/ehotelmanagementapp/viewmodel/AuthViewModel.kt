package com.example.ehotelmanagementapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ehotelmanagementapp.model.UserRole
import com.example.ehotelmanagementapp.utils.ValidationUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
    data class Success(val userId: String, val role: UserRole = UserRole.CUSTOMER) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    init {
        // Check current auth state when ViewModel is created
        auth.currentUser?.let { user ->
            checkUserRole(user.uid)
        }
    }

    private fun checkUserRole(userId: String) {
        viewModelScope.launch {
            try {
                val userDoc = firestore.collection("users")
                    .document(userId)
                    .get()
                    .await()
                val roleString = userDoc.getString("role") ?: UserRole.CUSTOMER.name
                val role = UserRole.valueOf(roleString)
                Log.d("AuthViewModel", "User role found: $role")
                _authState.value = AuthState.Success(userId, role)
            } catch (e: Exception) {
                Log.d("AuthViewModel", "Error checking user role", e)
                _authState.value = AuthState.Error(e.message ?: "Failed to get user role")

            }
        }
    }

    fun login(email: String, password: String) {

        /*  when {
              !ValidationUtils.isValidEmail(email) -> {
                  _authState.value = AuthState.Error("Invalid Email format")
                  return
              }

              password.isEmpty() -> {
                  _authState.value = AuthState.Error("Password cannot be empty")
                  return
              }
          }*/
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                Log.d("AuthViewModel", "Attempting login with email: $email")
                val result = auth.signInWithEmailAndPassword(email, password).await()
                result.user?.let { user ->
                    checkUserRole(user.uid)
                    /* Log.d("AuthViewModel", "Login successful, fetching user role")
                     // Fetch the user from firestore
                     val userDoc = firestore.collection("users")
                         .document(user.uid)
                         .get()
                         .await()
                     val roleString = userDoc.getString("role") ?: UserRole.CUSTOMER.name
                     val role = UserRole.valueOf(roleString)

                     Log.d("AuthViewModel", "User role fetched : $role")
                     _authState.value = AuthState.Success(user.uid, role)*/

                } ?: run {
                    Log.d("AuthViewModel", "Login successful but user is null")
                    _authState.value = AuthState.Error("Login failed : User is null")
                }
            } catch (e: Exception) {
//                val errorMessage = when (e.message) {
////                    "ERROR_WRONG_PASSWORD" -> "Incorrect password"
////                    "ERROR_USER_NOT_FOUND" -> "No account found with this email"
////                    "ERROR_USER_DISABLED" -> "This account has been disabled"
////                    "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Please try again later"
//                    else -> "Login failed: ${e.message}"
//                }
                Log.e("AuthViewModel", "Login error", e)
                _authState.value = AuthState.Error(e.message ?: "login failed")
            }
        }
    }

    fun register(
        email: String,
        password: String,
        name: String,
        role: UserRole = UserRole.CUSTOMER
    ) {
        when {
            !ValidationUtils.isValidEmail(email) -> {
                _authState.value = AuthState.Error("Invalid email format")
                return
            }

            !ValidationUtils.isValidPassword(password) -> {
                _authState.value =
                    AuthState.Error(ValidationUtils.getPasswordErrorMessage(password))
                return
            }

            name.length < 2 -> {
                _authState.value = AuthState.Error("Name must be at least 2 characters")
                return
            }
        }
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                result.user?.let { user ->
                    // Create user document in Firestore
                    val userData = hashMapOf(
                        "email" to email,
                        "name" to name,
                        "role" to role,
                        "createdAt" to System.currentTimeMillis()
                    )
                    firestore.collection("users")
                        .document(user.uid)
                        .set(userData)
                        .await()

                    _authState.value = AuthState.Success(user.uid, role)
                } ?: run {
                    _authState.value = AuthState.Error("Registration failed")
                }
            } catch (e: Exception) {
                val errorMessage = when (e.message) {
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "An account already exits with this email"
                    "ERROR_WEAK_PASSWORD" -> "Password is too weak"
                    else -> "Registration failed: ${e.message}"

                }
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }


    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Initial
    }

    fun getCurrentAuthState() {
        auth.currentUser?.let { user ->
            if (_authState.value !is AuthState.Success) {
                checkUserRole(user.uid)
            }
        }?: run {
            _authState.value = AuthState.Initial
        }
    }

    fun resetPassword(email: String) {
        if (!ValidationUtils.isValidEmail(email)) {
            _authState.value = AuthState.Error("Invalid email format")
            return
        }

        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                auth.sendPasswordResetEmail(email).await()
                _authState.value = AuthState.Initial
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Failed to send reset email")
            }
        }
    }
}
