package com.example.ehotelmanagementapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ehotelmanagementapp.model.UserRole
import com.example.ehotelmanagementapp.navigation.Screen
import com.example.ehotelmanagementapp.ui.components.AuthButton
import com.example.ehotelmanagementapp.ui.components.AuthTextField
import com.example.ehotelmanagementapp.utils.ValidationUtils
import com.example.ehotelmanagementapp.viewmodel.AuthState
import com.example.ehotelmanagementapp.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                when ((authState as AuthState.Success).role) {
                    UserRole.STAFF -> navController.navigate(Screen.StaffHome.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }

                    UserRole.CUSTOMER -> navController.navigate(Screen.CustomerHome.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }

                    else -> {} // Handle other roles if needed
                }
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Login") }) }
    )
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AuthTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false
                },
                label = "Email",
                keyboardType = KeyboardType.Email,
                isError = emailError,
                modifier = Modifier.fillMaxWidth()
            )

            AuthTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = false
                },
                label = "Password",
                isPassword = true,
                isError = passwordError,
                modifier = Modifier.fillMaxWidth()
            )

            if (authState is AuthState.Error) {
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            AuthButton(
                text = "Login",
                onClick = {
                    emailError = !ValidationUtils.isValidEmail(email)
                    passwordError = password.isEmpty()
                    if (!emailError && !passwordError) {
                        viewModel.login(email, password)
                    }
                },
                isLoading = authState is AuthState.Loading,
                modifier = Modifier.fillMaxWidth()
            )
            TextButton(
                onClick = { navController.navigate(Screen.Register.route) }
            ) {
                Text("Don't Have an account? Register")
            }
            TextButton(
                onClick =
                {
                    if (ValidationUtils.isValidEmail(email)) {
                        viewModel.resetPassword(email)
                    } else {
                        emailError = true
                    }
                }
            ) {
                Text("Forget Password?")
            }
        }
    }

}




