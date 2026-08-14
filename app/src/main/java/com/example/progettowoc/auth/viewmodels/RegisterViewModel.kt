package com.example.progettowoc.auth.viewmodels

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progettowoc.auth.data.AuthRepositoryInterface
import com.example.progettowoc.auth.uiStates.RegisterUiState
import com.example.progettowoc.users.data.UserRole
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
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepositoryInterface
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()


    fun onRegisterClick() {
        resetErrors()
        if(validateForm()) {
            val state = uiState.value
            val role = state.role ?: return //serve perche in uistate è nullable, ma non dovrebbe mai essere null qui
            viewModelScope.launch {
                try {
                    _uiState.update {
                        it.copy(isLoading = true)
                    }

                    authRepository.registerUser(
                        state.email,
                        state.password,
                        state.name,
                        role
                    )

                    _uiState.update {
                        it.copy(isLoggedIn = true)
                    }
                } catch (e: AuthRestException) {
                    when (e.errorCode) {
                        AuthErrorCode.UserAlreadyExists -> _uiState.update { it.copy(emailError = "Email già in uso") }
                        AuthErrorCode.WeakPassword -> _uiState.update { it.copy(passwordError = "Password troppo debole") }
                        else -> _uiState.update { it.copy(generalError = "Qualcosa è andato storto") }
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            generalError = "Qualcosa è andato storto"
                        )
                    }
                } finally {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
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

    fun onConfirmPasswordChange(value: String) {
        _uiState.update {
            it.copy(confirmPassword = value)
        }
    }

    fun onNameChange(value: String) {
        _uiState.update {
            it.copy(name = value)
        }
    }

    fun onRoleChange(value: UserRole) {
        _uiState.update {
            it.copy(role = value)
        }
    }


    private fun validateForm(): Boolean {
        val validatedState = RegisterFormValidator.validate(uiState.value)

        _uiState.value = validatedState

        return listOf(
            validatedState.nameError,
            validatedState.emailError,
            validatedState.roleError,
            validatedState.passwordError,
            validatedState.confirmPasswordError
        ).all { it == null }
    }


    private fun resetErrors() {
        _uiState.update {
            it.copy(
                emailError = null,
                passwordError = null,
                confirmPasswordError = null,
                nameError = null,
                roleError = null,
                generalError = null
            )
        }
    }
}


private object RegisterFormValidator {
    fun validate(state: RegisterUiState): RegisterUiState {

        val nameError =
            if (state.name.trim().split("\\s+".toRegex()).size < 2)
                "Inserisci nome e cognome"
            else null

        val emailError =
            if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches())
                "Email non valida"
            else null

        val roleError =
            if (state.role == null)
                "Seleziona un ruolo"
            else null

        val passwordError =
            if (state.password.length < 8)
                "Minimo 8 caratteri"
            else null

        val confirmPasswordError =
            if (state.password != state.confirmPassword)
                "Le password non coincidono"
            else null

        return state.copy(
            nameError = nameError,
            emailError = emailError,
            roleError = roleError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError
        )
    }
}