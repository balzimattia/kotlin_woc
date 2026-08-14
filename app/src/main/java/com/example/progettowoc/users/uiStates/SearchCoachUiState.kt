package com.example.progettowoc.users.uiStates

import com.example.progettowoc.users.data.User

data class SearchCoachUiState(
    val search: String = "",
    val coachesList: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)