package com.example.ehotelmanagementapp.utils

object ValidationUtils {
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        // At least 6 characters, containing at least one number and one letter
        return password.length >= 6 &&
                password.any { it.isDigit() } &&
                password.any { it.isLetter() }
    }

    fun getPasswordErrorMessage(password: String): String {
        return when {
            password.length < 6 -> "Password must be at least 6 characters"
            !password.any { it.isDigit() } -> "Password must contain at least one number"
            !password.any { it.isLetter() } -> "Password must contain at least one letter"
            else -> ""
        }
    }
}