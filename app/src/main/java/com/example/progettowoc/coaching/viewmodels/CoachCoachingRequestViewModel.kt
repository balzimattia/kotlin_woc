package com.example.progettowoc.coaching.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progettowoc.coaching.data.CoachingRequestRepositoryInterface
import com.example.progettowoc.users.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CoachCoachingRequestViewModel @Inject constructor(
    private val coachingRequestRepository: CoachingRequestRepositoryInterface
): ViewModel() {

    private val _coachingRequestsList = MutableStateFlow<List<User>>(emptyList())
    val coachingRequestsList: StateFlow<List<User>> = _coachingRequestsList.asStateFlow()

    private val _isLoadingRequestsList = MutableStateFlow<Boolean>(false)
    val isLoadingRequestsList: StateFlow<Boolean> = _isLoadingRequestsList.asStateFlow()

    private val _errorRequestMessage = MutableSharedFlow<String>()
    val errorRequestMessage: SharedFlow<String> = _errorRequestMessage.asSharedFlow()


    init {
        retrieveCoachingRequestsList()
    }

    fun retrieveCoachingRequestsList() {
        viewModelScope.launch {
            try {
                _isLoadingRequestsList.value = true

                val list = coachingRequestRepository.retrieveRequestsList()
                _coachingRequestsList.value = list
            }
            catch (e: Exception) {
                _coachingRequestsList.value = emptyList()
            } finally {
                _isLoadingRequestsList.value = false
            }
        }
    }

    fun updateRequest(isAccepted: Boolean, clienteId: String) {
        viewModelScope.launch {
            try {
                coachingRequestRepository.updateRequest(isAccepted = isAccepted, clienteId = clienteId)
                retrieveCoachingRequestsList()
            }
            catch (e: Exception) {
                _errorRequestMessage.emit("Qualcosa è andato storto")
            }
        }
    }
}