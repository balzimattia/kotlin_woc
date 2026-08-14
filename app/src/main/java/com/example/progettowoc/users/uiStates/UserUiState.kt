package com.example.progettowoc.users.uiStates

import com.example.progettowoc.users.data.UserRole

data class UserUiState(
    val email: String = "",
    val name: String = "",
    val role: UserRole? = null, // "coach" o "cliente"
    val coachId: String? = null, //in caso user è coach è null oppure possiamo mettere anche solo il nome o l'oggetto user
    val clientiId: List<String> = emptyList() //in caso user è cliente è vuota, same di sopra
)