package com.example.ehotelmanagementapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }


    val authState by viewModel.authState.collectAsState()

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
    Scaffold(topBar = {
        TopAppBar(title = { Text("Register") }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
        })
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {
            AuthTextField(
                value = name, onValueChange = {
                    name = it
                    nameError = false
                },
                label = "Full Name",
                isError = nameError,
                modifier = Modifier.fillMaxWidth()
            )
            AuthTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false
                },
                label = "Email",
                isError = emailError,
                keyboardType = KeyboardType.Email,
                modifier = Modifier.fillMaxWidth()
            )
            AuthTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = false
                    confirmPasswordError = password != confirmPassword
                },
                label = "Password",
                isPassword = true,
                isError = passwordError,
                modifier = Modifier.fillMaxWidth()
            )

            AuthTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPassword = password != confirmPassword
                },
                label = "Confirm Password",
                isPassword = true,
                isError = confirmPasswordError,
                modifier = Modifier.fillMaxWidth()
            )

            if (authState is AuthState.Error) {
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            AuthButton(
                text = "Register", onClick = {
                    nameError = name.length < 2
                    emailError = !ValidationUtils.isValidEmail(email)
                    passwordError = !ValidationUtils.isValidPassword(password)
                    confirmPasswordError = password != confirmPassword

                    if (!nameError && !emailError && !passwordError && !confirmPasswordError) {
                        viewModel
                    }
                }, isLoading = authState is AuthState.Loading, modifier = Modifier.fillMaxWidth()
            )
            TextButton(onClick = { navController.navigateUp() }) {
                Text("Already have an account? Login")
            }
        }

    }
}