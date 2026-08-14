package com.example.progettowoc.coaching.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progettowoc.auth.data.AuthRepositoryInterface
import com.example.progettowoc.coaching.data.CoachingRequestRepositoryInterface
import com.example.progettowoc.coaching.data.RequestStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClienteCoachingRequestViewModel @Inject constructor(
    private val coachingRequestRepository: CoachingRequestRepositoryInterface,
    private val authRepository: AuthRepositoryInterface
): ViewModel() {

    private val _requestStatus = MutableStateFlow<RequestStatus?>(null)
    val requestStatus: StateFlow<RequestStatus?> = _requestStatus.asStateFlow()

    private val _isLoadingRequestStatus = MutableStateFlow<Boolean>(false)
    val isLoadingRequestStatus: StateFlow<Boolean> = _isLoadingRequestStatus.asStateFlow()

    private val _requestErrorMessage = MutableSharedFlow<String>()
    val requestErrorMessage: SharedFlow<String> = _requestErrorMessage.asSharedFlow()


    fun addRequest(coachId: String) {
        viewModelScope.launch {
            try {
                coachingRequestRepository.addRequest(coachId = coachId)
            }
            catch (e: PostgrestRestException) {
                _requestErrorMessage.emit("Qualcosa è andato storto")
            }
        }
    }


    fun getPendingRequest() {
        viewModelScope.launch {
            try {
                _isLoadingRequestStatus.value = true

                val id = authRepository.currentUser.value?.id ?: return@launch
                val status = coachingRequestRepository.getClientePendingRequest(clienteId = id)
                _requestStatus.value = status
            } catch (e: Exception) {
                _requestStatus.value = null
            } finally {
                _isLoadingRequestStatus.value = false
            }
        }
    }


    fun deletePendingRequest() {
        viewModelScope.launch {
            try {
                coachingRequestRepository.deletePendingRequest()
                getPendingRequest()
            } catch (e: Exception) { }
        }
    }
}