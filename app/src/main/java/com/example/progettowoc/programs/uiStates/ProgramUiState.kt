package com.example.progettowoc.programs.uiStates

import com.example.progettowoc.programs.data.ProgramSheet

data class ProgramUiState(
    var currentProgram: ProgramSheet? = null,
    var currentWeekNumber: Int? = null,
    var currentDayNumber: Int? = null,
    var isLoadingScreen: Boolean = false,
    var isLoadingWeeks: Boolean = false
)