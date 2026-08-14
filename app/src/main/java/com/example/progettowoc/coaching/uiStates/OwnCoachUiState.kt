package com.example.progettowoc.coaching.uiStates

import com.example.progettowoc.users.data.User

data class OwnCoachUiState(
    val ownCoach: User? = null,
    val isLoadingOwnCoach: Boolean = false,
    val ownCoachLoadError: Boolean = false
)