package com.example.ehotelmanagementapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ehotelmanagementapp.navigation.Screen
import com.example.ehotelmanagementapp.ui.components.AuthButton
import com.example.ehotelmanagementapp.ui.components.AuthTextField
import com.example.ehotelmanagementapp.utils.ValidationUtils
import com.example.ehotelmanagementapp.viewmodel.AuthState
import com.example.ehotelmanagementapp.viewmodel.AuthViewModel
import com.stripe.android.view.CardValidCallback


data class FieldState(
    val value: String = "",
    val errorMessage: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf(FieldState()) }
    var email by remember { mutableStateOf(FieldState()) }
    var password by remember { mutableStateOf(FieldState()) }
    var confirmPassword by remember { mutableStateOf(FieldState()) }

    /*var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }*/

    // Validation Funcitons


    val authState by viewModel.authState.collectAsState()

    fun validateName(value: String): String? {
        return when {
            value.isBlank() -> "Name is required"
            value.length < 2 -> "Name must be at least 2 characters"
            else -> null
        }
    }

    fun validateEmail(value: String): String? {
        return when {
            value.isBlank() -> "Email is required"
            !ValidationUtils.isValidEmail(value) -> "Please enter a valid email"
            else -> null
        }
    }

    fun validatePassword(value: String): String? {
        return when {
            value.isBlank() -> "Password is required"
            value.length < 6 -> "Password must be at least 6 characters"
            !value.any { it.isDigit() } -> "Password must contain at least one number"
            !value.any { it.isDigit() } -> "Password must contain at least one letter"
            else -> null
        }
    }

    fun validateConfirmPassword(value: String): String? {
        return when {
            value.isBlank() -> "Please confirm your password"
            value != password.value -> "Passwords do not match"
            else -> null
        }
    }




    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                navController.navigate(Screen.CustomerHome.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
            }
            else -> {}
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                })
        }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)

        ) {

            // Name field
            AuthTextField(
                value = name.value,
                onValueChange = {
                    name = name.copy(
                        value = it,
                        errorMessage = validateName(it)
                    )

                },
                label = "Full Name",
                modifier = Modifier.fillMaxWidth()
            )
            name.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
            // Email field
            AuthTextField(
                value = email.value,
                onValueChange = {
                    email = email.copy(
                        value = it,
                        errorMessage = validateEmail(it)
                    )

                },
                label = "Email",
                keyboardType = KeyboardType.Email,
                modifier = Modifier.fillMaxWidth()
            )
            email.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
            // Password field
            AuthTextField(
                value = password.value,
                onValueChange = {
                    password = password.copy(
                        value = it,
                        errorMessage = validatePassword(it)
                    )
                    // Revalidate confirm password when password changes
                    if (confirmPassword.value.isNotEmpty()) {
                        confirmPassword = confirmPassword.copy(
                            errorMessage = validateConfirmPassword(confirmPassword.value)
                        )
                    }

                },
                label = "Password",
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )
            password.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
            // Confirm Password Field
            AuthTextField(
                value = confirmPassword.value,
                onValueChange = {
                    confirmPassword = confirmPassword.copy(
                        value = it,
                        errorMessage = validateConfirmPassword(it)
                    )

                },
                label = "Confirm Password",
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )
            confirmPassword.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (authState is AuthState.Error) {
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            AuthButton(
                text = "Register",
                onClick = {
                    // validate all fields before submission
                    name = name.copy(errorMessage = validateName(name.value))
                    email = email.copy(errorMessage = validateEmail(email.value))
                    password = password.copy(errorMessage = validatePassword(password.value))
                    confirmPassword = confirmPassword.copy(errorMessage = validateConfirmPassword(confirmPassword.value))
                    // only Proceed if all validation pass
                    if (listOf(name, email, password, confirmPassword).all { it.errorMessage == null }) {
                        viewModel.register(email.value, password.value, name.value)
                    }
                },
                isLoading = authState is AuthState.Loading,
                enabled = listOf(name, email, password, confirmPassword).all { it.value.isNotEmpty() },
                modifier = Modifier.fillMaxWidth()
            )

            TextButton(onClick = { navController.navigateUp() }) {
                Text("Already have an account? Login")
            }
        }

    }
}