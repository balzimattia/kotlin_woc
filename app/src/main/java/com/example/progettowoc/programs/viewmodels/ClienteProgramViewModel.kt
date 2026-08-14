package com.example.progettowoc.programs.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progettowoc.auth.data.AuthRepositoryInterface
import com.example.progettowoc.programs.uiStates.ProgramUiState
import com.example.progettowoc.programs.data.ClienteProgramsRepositoryInterface
import com.example.progettowoc.programs.data.Day
import com.example.progettowoc.programs.data.Exercise
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClienteProgramViewModel @Inject constructor(
    private val programsRepository: ClienteProgramsRepositoryInterface,
    private val authRepository: AuthRepositoryInterface
): ViewModel() {
    private val _currentProgramUiState = MutableStateFlow(ProgramUiState())
    val currentProgramUiState: StateFlow<ProgramUiState> = _currentProgramUiState.asStateFlow()

    private val _latestProgramNumState = MutableStateFlow(0)
    val latestProgramNumState: StateFlow<Int> = _latestProgramNumState.asStateFlow()

    private val _isDayCompletedLoading = MutableStateFlow(false)
    val isDayCompletedLoading: StateFlow<Boolean> = _isDayCompletedLoading.asStateFlow()

    private val _exerciseErrorMessage = MutableSharedFlow<String>()
    val exerciseErrorMessage: SharedFlow<String> = _exerciseErrorMessage.asSharedFlow()



    init {
        loadInitialData()
    }


    // carica il programma
    fun loadInitialData() {
        viewModelScope.launch {
            try {
                _currentProgramUiState.update { it.copy(isLoadingScreen = true) }

                val id = authRepository.currentUser.value?.id ?: return@launch
                val n = programsRepository.getLatestProgramNumber(clienteId = id)
                _latestProgramNumState.value = n
                if (n > 0) {
                    _currentProgramUiState.update {
                        it.copy(
                            currentProgram = programsRepository.getProgram(
                                clienteId = id,
                                programNumber = n
                            )
                        )
                    }
                }

                _currentProgramUiState.update { it.copy(isLoadingScreen = false) }
            } catch (e: Exception) {
                _currentProgramUiState.update {
                    ProgramUiState()
                }
            }
        }
    }


    fun setCurrentDay(weekNumber: Int, dayNumber: Int) {
        _currentProgramUiState.update {
            it.copy(currentWeekNumber = weekNumber, currentDayNumber = dayNumber)
        }
    }


    fun getProgram(programNumber: Int) {
        viewModelScope.launch {
            try {
                _currentProgramUiState.update {
                    it.copy(isLoadingWeeks = true)
                }

                val id = authRepository.currentUser.value?.id ?: return@launch
                val program = programsRepository.getProgram(clienteId = id, programNumber = programNumber)

                _currentProgramUiState.update {
                    it.copy(currentProgram = program)
                }
            } catch (e: Exception) {
            } finally {
                _currentProgramUiState.update {
                    it.copy(isLoadingWeeks = false)
                }
            }
        }
    }


    fun getDay(weekNumber: Int, dayNumber: Int): Day? {
        return _currentProgramUiState.value.currentProgram?.weeks
            ?.firstOrNull { it.number == weekNumber }
            ?.days
            ?.firstOrNull { it.number == dayNumber }
    }


    fun getNextDay(): Day? {
        _currentProgramUiState.value.currentProgram?.weeks?.sortedBy { it.number }?.forEach { week ->
            week.days.sortedBy { it.number }.forEach { day ->
                if (!day.isCompleted) {
                    _currentProgramUiState.update { it ->
                        it.copy(
                            currentWeekNumber = week.number,
                            currentDayNumber = day.number
                        )
                    }
                    return day
                }
            }
        }
        return null
    }


    fun getExercise(weekNumber: Int, dayNumber: Int, exerciseIndex: Int): Exercise? {
        return getDay(weekNumber, dayNumber)?.exercises?.getOrNull(exerciseIndex)
    }


    fun updateExercise(weekNumber: Int, dayNumber: Int, index: Int, updated: Exercise) {
        _currentProgramUiState.update { state ->
            val program = state.currentProgram ?: return@update state
            state.copy(
                currentProgram = program.copy(
                    weeks = program.weeks.map { week ->
                        if (week.number != weekNumber) return@map week
                        week.copy(days = week.days.map { day ->
                            if (day.number != dayNumber) return@map day
                            day.copy(exercises = day.exercises.toMutableList().also {
                                it[index] = updated
                            })
                        })
                    }
                )
            )
        }
    }


    private suspend fun updateAndSaveExercise(
        weekNumber: Int,
        dayNumber: Int,
        exerciseIndex: Int,
        update: Exercise.() -> Exercise
    ) {
        val updated = _currentProgramUiState.value.currentProgram?.weeks
            ?.first { it.number == weekNumber }?.days
            ?.first { it.number == dayNumber }?.exercises[exerciseIndex]
            ?.update() ?: return

        updateExercise(weekNumber, dayNumber, exerciseIndex, updated)

        val sheet = _currentProgramUiState.value.currentProgram ?: return
        programsRepository.updateProgramAsCliente(sheet)
    }


    fun changeWeight(weekNumber: Int, dayNumber: Int, exerciseIndex: Int, newWeight: Float) {
        viewModelScope.launch {
            try {
                updateAndSaveExercise(
                    weekNumber,
                    dayNumber,
                    exerciseIndex
                ) { copy(weight = newWeight) }
            } catch (e: Exception) {
                _exerciseErrorMessage.emit("Errore durante il salvataggio del peso")
            }
        }
    }


    fun updateClienteComment(weekNumber: Int, dayNumber: Int, exerciseIndex: Int, comment: String) {
        viewModelScope.launch {
            try {
                updateAndSaveExercise(
                    weekNumber,
                    dayNumber,
                    exerciseIndex
                ) { copy(clienteComment = comment) }
            } catch (e: Exception) {
                _exerciseErrorMessage.emit("Errore durante il salvataggio del commento")
            }
        }
    }


    fun completeDay(weekNumber: Int, dayNumber: Int) {
        viewModelScope.launch {
            try {
                _isDayCompletedLoading.value = true
                updateAndSaveDay(weekNumber, dayNumber) { copy(isCompleted = true) }
            } catch (e: Exception) {
            } finally {
                _isDayCompletedLoading.value = false
            }
        }
    }


    private suspend fun updateAndSaveDay(
        weekNumber: Int,
        dayNumber: Int,
        update: Day.() -> Day
    ) {
        val updatedProgram = _currentProgramUiState.value.currentProgram?.copy(
            weeks = _currentProgramUiState.value.currentProgram?.weeks?.map { week ->
                if (week.number != weekNumber) return@map week
                week.copy(days = week.days.map { day ->
                    if (day.number != dayNumber) return@map day
                    day.update()
                })
            } ?: return
        ) ?: return

        _currentProgramUiState.update {
            it.copy(currentProgram = updatedProgram)
        }
        programsRepository.updateProgramAsCliente(updatedProgram)
    }
}