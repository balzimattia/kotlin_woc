package com.example.progettowoc.programs.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progettowoc.programs.uiStates.DayUiState
import com.example.progettowoc.programs.uiStates.EditProgramUiState
import com.example.progettowoc.programs.uiStates.ExerciseUiState
import com.example.progettowoc.programs.uiStates.WeekUiState
import com.example.progettowoc.programs.data.CoachProgramsRepositoryInterface
import com.example.progettowoc.programs.data.Day
import com.example.progettowoc.programs.data.Exercise
import com.example.progettowoc.programs.data.ProgramParser
import com.example.progettowoc.programs.data.ProgramSheet
import com.example.progettowoc.programs.data.Week
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
import kotlin.collections.plus

@HiltViewModel
class EditProgramViewModel @Inject constructor(
    private val programsRepository: CoachProgramsRepositoryInterface
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProgramUiState())
    val uiState: StateFlow<EditProgramUiState> = _uiState.asStateFlow()

    private val _editErrorMessage = MutableSharedFlow<String>()
    val editErrorMessage: SharedFlow<String> = _editErrorMessage.asSharedFlow()


    private var initialized = false
    fun init(clienteId: String, programNumber: Int?) {
        if (initialized) return
        initialized = true

        if (programNumber == null) {
            _uiState.value = EditProgramUiState(
                isNewProgram = true,
                weeks = emptyList()
            )
        } else {
            loadExisting(clienteId, programNumber)
        }
    }


    private fun loadExisting(clienteId: String, programNumber: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val sheet = programsRepository.getProgram(clienteId, programNumber)
                _uiState.update {
                    it.copy(
                        isNewProgram = false,
                        programNumber = programNumber,
                        weeks = sheet.weeks.map { week ->
                            WeekUiState(
                                number = week.number,
                                days = week.days.map { day ->
                                    DayUiState(
                                        number = day.number,
                                        isCompleted = day.isCompleted,
                                        exercises = day.exercises.map { ex ->
                                            ex.toUiState()
                                        }.toMutableList()
                                    )
                                }.toMutableList()
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                _editErrorMessage.emit(e.message ?: "Qualcosa è andato storto")
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }


    fun addWeek() {
        val nextNum = (_uiState.value.weeks.maxOfOrNull { it.number } ?: 0) + 1
        _uiState.update {
            it.copy(weeks = it.weeks + WeekUiState(number = nextNum))
        }
    }


    fun removeWeek(weekNumber: Int) {
        _uiState.update {
            it.copy(weeks = it.weeks.filter { w -> w.number != weekNumber })
        }
    }


    fun addDay(weekNumber: Int) {
        _uiState.update { state ->
            state.copy(weeks = state.weeks.map { week ->
                if (week.number != weekNumber) return@map week
                val nextNum = (week.days.maxOfOrNull { it.number } ?: 0) + 1
                week.copy(days = (week.days + DayUiState(number = nextNum, isCompleted = false)).toMutableList())
            })
        }
    }


    fun removeDay(weekNumber: Int, dayNumber: Int) {
        _uiState.update { state ->
            state.copy(weeks = state.weeks.map { week ->
                if (week.number != weekNumber) return@map week
                week.copy(days = week.days.filter { it.number != dayNumber }.toMutableList())
            })
        }
    }


    fun addExercise(weekNumber: Int, dayNumber: Int) {
        _uiState.update { state ->
            state.copy(weeks = state.weeks.map { week ->
                if (week.number != weekNumber) return@map week
                week.copy(days = week.days.map { day ->
                    if (day.number != dayNumber) return@map day
                    day.copy(exercises = (day.exercises + ExerciseUiState()).toMutableList())
                }.toMutableList())
            })
        }
    }


    fun updateExercise(weekNumber: Int, dayNumber: Int, index: Int, updated: ExerciseUiState) {
        _uiState.update { state ->
            state.copy(weeks = state.weeks.map { week ->
                if (week.number != weekNumber) return@map week
                week.copy(days = week.days.map { day ->
                    if (day.number != dayNumber) return@map day
                    val newExercises = day.exercises.toMutableList()
                    newExercises[index] = updated
                    day.copy(exercises = newExercises)
                }.toMutableList())
            })
        }
    }


    fun removeExercise(weekNumber: Int, dayNumber: Int, index: Int) {
        _uiState.update { state ->
            state.copy(weeks = state.weeks.map { week ->
                if (week.number != weekNumber) return@map week
                week.copy(days = week.days.map { day ->
                    if (day.number != dayNumber) return@map day
                    day.copy(exercises = day.exercises.filterIndexed { i, _ -> i != index }.toMutableList())
                }.toMutableList())
            })
        }
    }


    fun saveProgram(clienteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val state = _uiState.value
                if (state.isNewProgram) {
                    val sheet = state.toProgamSheet().copy(number = 0)
                    programsRepository.addProgram(clienteId, sheet)
                } else {
                    val sheet = state.toProgamSheet()
                    programsRepository.updateProgram(clienteId, sheet)
                }
                _uiState.update { it.copy(saveSuccess = true) }
            } catch (e: Exception) {
                _editErrorMessage.emit("Errore durante il salvataggio")
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }


    fun loadFromFile(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val newWeeks = ProgramParser.toListWeek(context = context, programSheet = uri)
                _uiState.update { state ->
                    val existingWeeks = state.weeks.toMutableList()

                    newWeeks.forEach { newWeek ->
                        val existingIndex =
                            existingWeeks.indexOfFirst { it.number == newWeek.number }
                        val newWeekUiState = WeekUiState(
                            number = newWeek.number,
                            days = newWeek.days.map { day ->
                                DayUiState(
                                    number = day.number,
                                    isCompleted = day.isCompleted,
                                    exercises = day.exercises.map { it.toUiState() }
                                        .toMutableList(),
                                )
                            }.toMutableList()
                        )

                        if (existingIndex != -1) {
                            existingWeeks[existingIndex] = newWeekUiState
                        } else {
                            existingWeeks.add(newWeekUiState)
                        }
                    }

                    state.copy(weeks = existingWeeks.sortedBy { it.number })
                }
            } catch (e: Exception) {
                _editErrorMessage.emit(e.message ?: "Formato non supportato")
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }


    fun exportToFile(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val sheet = _uiState.value.toProgamSheet()
                ProgramParser.toProgramSpreadSheet(context = context, program = sheet, uri = uri)
            } catch (e: Exception) {
                _editErrorMessage.emit(e.message ?: "Qualcosa è andato storto")
            }
        }
    }
}


private fun Exercise.toUiState() = ExerciseUiState(
    name = name, sets = sets, reps = reps,
    rest = rest, weight = weight,
    coachComment = coachComment, clienteComment = clienteComment
)

private fun ExerciseUiState.toDomain() = Exercise(
    name = name, sets = sets, reps = reps,
    rest = rest, weight = weight,
    coachComment = coachComment, clienteComment = clienteComment
)

private fun EditProgramUiState.toProgamSheet() = ProgramSheet(
    number = programNumber,
    weeks = weeks.map { week ->
        Week(
            number = week.number,
            days = week.days.map { day ->
                Day(
                    number = day.number,
                    isCompleted = day.isCompleted,
                    exercises = day.exercises.map { it.toDomain() }
                )
            }
        )
    }
)