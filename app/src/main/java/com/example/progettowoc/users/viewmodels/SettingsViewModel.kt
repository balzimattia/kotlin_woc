package com.example.progettowoc.users.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progettowoc.auth.data.AuthRepositoryInterface
import com.example.progettowoc.notifications.NotificationType
import com.example.progettowoc.notifications.data.NotificationPreferencesDataStore
import com.example.progettowoc.users.data.SettingPreferencesDataStore
import com.example.progettowoc.users.uiStates.ChangePasswordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val notificationPreferencesDataStore: NotificationPreferencesDataStore,
    private val settingPreferencesDataStore: SettingPreferencesDataStore,
    private val authRepository: AuthRepositoryInterface
) : ViewModel() {

    val notificationPreferences: StateFlow<Map<NotificationType, Boolean>> =
        notificationPreferencesDataStore.getAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyMap()
            )

    val isDarkTheme: StateFlow<Boolean> = settingPreferencesDataStore.darkThemeFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)


    private val _changePasswordUiState = MutableStateFlow(ChangePasswordUiState())
    val changePasswordUiState: StateFlow<ChangePasswordUiState> = _changePasswordUiState.asStateFlow()

    fun setNotificationEnabled(type: NotificationType, enabled: Boolean) {
        viewModelScope.launch {
            notificationPreferencesDataStore.setEnabled(type, enabled)
        }
    }

    fun onNewPasswordChange(value: String) {
        _changePasswordUiState.update { it.copy(newPassword = value, newPasswordError = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _changePasswordUiState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    fun onChangePasswordClick() {
        if (validatePasswordForm()) {
            viewModelScope.launch {
                try {
                    _changePasswordUiState.update { it.copy(isLoading = true) }

                    authRepository.updatePassword(_changePasswordUiState.value.newPassword)

                    _changePasswordUiState.update { it.copy(saveSuccess = true) }
                } catch (e: AuthRestException) {
                    if (e.errorCode == AuthErrorCode.SamePassword) _changePasswordUiState.update { it.copy(newPasswordError = "Nuova password uguale a quella corrente") }
                } catch (e: Exception) {
                    _changePasswordUiState.update { it.copy(newPasswordError = "Qualcosa è andato storto") }
                } finally {
                    _changePasswordUiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun validatePasswordForm(): Boolean {
        val state = _changePasswordUiState.value

        val newPasswordError = if (state.newPassword.length < 8) "Minimo 8 caratteri" else null
        val confirmPasswordError = if (state.newPassword != state.confirmPassword) "Le password non coincidono" else null

        _changePasswordUiState.update {
            it.copy(
                newPasswordError = newPasswordError,
                confirmPasswordError = confirmPasswordError
            )
        }

        return newPasswordError == null && confirmPasswordError == null
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            settingPreferencesDataStore.setDarkTheme(enabled)
        }
    }
}