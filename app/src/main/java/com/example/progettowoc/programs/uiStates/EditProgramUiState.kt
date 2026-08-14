package com.example.progettowoc.programs.uiStates

data class ExerciseUiState(
    val name: String = "",
    val sets: Int = 0,
    val reps: Int = 0,
    val rest: Int = 0,
    val weight: Float = 0f,
    val coachComment: String = "",
    val clienteComment: String = ""
)

data class DayUiState(
    val number: Int,
    val isCompleted: Boolean,
    val exercises: MutableList<ExerciseUiState> = mutableListOf()
)

data class WeekUiState(
    val number: Int,
    val days: MutableList<DayUiState> = mutableListOf()
)

data class EditProgramUiState(
    val isLoading: Boolean = false,
    val isNewProgram: Boolean = true,
    val programNumber: Int = 0,
    val weeks: List<WeekUiState> = emptyList(),
    val saveSuccess: Boolean = false
)