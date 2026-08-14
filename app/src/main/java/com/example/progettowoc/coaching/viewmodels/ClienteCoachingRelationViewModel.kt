package com.example.progettowoc.coaching.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progettowoc.auth.data.AuthRepositoryInterface
import com.example.progettowoc.coaching.data.CoachingRelationRepositoryInterface
import com.example.progettowoc.coaching.uiStates.OwnCoachUiState
import com.example.progettowoc.users.data.User
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
class ClienteCoachingRelationViewModel @Inject constructor(
    private val coachingRepository: CoachingRelationRepositoryInterface,
    private val authRepository: AuthRepositoryInterface
): ViewModel() {
    private val _clienteOwnCoachUiState = MutableStateFlow(OwnCoachUiState())
    val clienteOwnCoachUiState: StateFlow<OwnCoachUiState> = _clienteOwnCoachUiState.asStateFlow()

    private val _ownCoachErrorMessage = MutableSharedFlow<String>()
    val ownCoachErrorMessage: SharedFlow<String> = _ownCoachErrorMessage.asSharedFlow()


    fun getClienteOwnCoach() {
        viewModelScope.launch {
            try {
                _clienteOwnCoachUiState.update { it.copy(isLoadingOwnCoach = true, ownCoachLoadError = false) }

                val id = authRepository.currentUser.value?.id ?: return@launch
                val coach = coachingRepository.getClienteOwnCoach(id)

                _clienteOwnCoachUiState.update { it.copy(ownCoach = coach) }
            } catch (e: Exception) {
                _clienteOwnCoachUiState.update { it.copy(ownCoachLoadError = true) }
            } finally {
                _clienteOwnCoachUiState.update {
                    it.copy(isLoadingOwnCoach = false)
                }
            }
        }
    }


    fun removeCoachingRelation() {
        viewModelScope.launch {
            try {
                val id = authRepository.currentUser.value?.id ?: return@launch
                coachingRepository.removeCoachingRelation(clienteId = id)
                _clienteOwnCoachUiState.update { it.copy(ownCoach = null) }
            } catch (e: Exception) {
                _ownCoachErrorMessage.emit("Qualcosa è andato storto")
            }
        }
    }
}