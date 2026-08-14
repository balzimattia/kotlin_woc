package com.example.progettowoc.auth.uiStates

import com.example.progettowoc.users.data.UserRole

data class RegisterUiState(
    val email: String = "",
    val emailError: String? = null,

    val password: String = "",
    val passwordError: String? = null,

    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,

    val name: String = "",
    val nameError: String? = null,

    val role: UserRole? = null,
    val roleError: String? = null,

    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val generalError: String? = null
)