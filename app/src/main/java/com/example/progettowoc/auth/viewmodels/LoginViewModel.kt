package com.example.progettowoc.auth.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progettowoc.auth.data.AuthRepositoryInterface
import com.example.progettowoc.auth.uiStates.LoginUiState
import com.example.progettowoc.users.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepositoryInterface
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()


    //private val currentUser: StateFlow<User?> = authRepository.currentUser


    fun onLoginClick() {
        resetError()
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(isLoading = true)
                }

                authRepository.login(uiState.value.email, uiState.value.password)

                _uiState.update {
                    it.copy(
                        isLoggedIn = true
                    )
                }
            } catch (e: AuthRestException) {
                when (e.errorCode) {
                    AuthErrorCode.UserNotFound, AuthErrorCode.InvalidCredentials -> _uiState.update {
                        it.copy(
                            errorMessage = "Email o password errati"
                        )
                    }
                    else -> _uiState.update { it.copy(errorMessage = "Qualcosa è andato storto") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Qualcosa è andato storto") }
            } finally {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }


    fun onEmailChange(value: String) {
        _uiState.update {
            it.copy(email = value)
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(password = value)
        }
    }

    private fun resetError() {
        _uiState.update {
            it.copy(
                errorMessage = null
            )
        }
    }
}