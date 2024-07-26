package com.example.yummy.utils

import android.util.Patterns

object Validations{
    fun isEmailValid(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    fun isUsernameValid(username: String): Boolean {
        val regex = "^[a-zA-Z0-9._-]{3,20}$"
        return username.matches(Regex(regex))
    }

    fun isPasswordValid(password: String): Boolean {
        // Customize this function based on your password validation criteria
        val regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$"
        return password.matches(Regex(regex))
    }
}