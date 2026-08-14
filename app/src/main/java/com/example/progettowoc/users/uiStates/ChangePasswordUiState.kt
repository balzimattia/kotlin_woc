package com.example.progettowoc.users.uiStates

data class ChangePasswordUiState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,
    val saveSuccess: Boolean = false,
    val isLoading: Boolean = false
)
