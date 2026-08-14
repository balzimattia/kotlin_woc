package com.example.progettowoc.auth.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progettowoc.auth.data.AuthRepositoryInterface
import com.example.progettowoc.users.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepositoryInterface
): ViewModel() {
    val sessionStatus: StateFlow<SessionStatus> = authRepository.sessionStatus

    val currentUser: StateFlow<User?> = authRepository.currentUser

    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut.asStateFlow()


    fun logout() {
        viewModelScope.launch {
            _isLoggingOut.value = true
            try {
                authRepository.logout()
            }
            catch (e: Exception) {
                throw Exception("qualcosa è andato stroto")
            }
        }
    }
}