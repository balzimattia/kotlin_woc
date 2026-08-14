package com.example.progettowoc.auth.uiStates

data class LoginUiState(
    val email: String = "",
    val password: String = "",

    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null
)