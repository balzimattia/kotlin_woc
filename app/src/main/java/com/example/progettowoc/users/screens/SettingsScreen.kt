package com.example.progettowoc.users.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.progettowoc.auth.viewmodels.AuthViewModel
import com.example.progettowoc.notifications.NotificationType
import com.example.progettowoc.ui.components.ElevatedCardComp
import com.example.progettowoc.ui.components.TextFieldComp
import com.example.progettowoc.ui.theme.ProgettoWOCTheme
import com.example.progettowoc.ui.theme.Red
import com.example.progettowoc.users.uiStates.ChangePasswordUiState
import com.example.progettowoc.users.viewmodels.SettingsViewModel
import com.example.progettowoc.users.data.UserRole

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val preferences by settingsViewModel.notificationPreferences.collectAsState()
    val changePasswordState by settingsViewModel.changePasswordUiState.collectAsState()
    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

    SettingsContent(
        role = currentUser?.role,
        isDarkTheme = isDarkTheme,
        preferences = preferences,
        changePasswordState = changePasswordState,
        onNotificationToggle = { type, enabled ->
            settingsViewModel.setNotificationEnabled(type, enabled)
        },
        onNewPasswordChange = { settingsViewModel.onNewPasswordChange(it) },
        onConfirmPasswordChange = { settingsViewModel.onConfirmPasswordChange(it) },
        onChangePasswordClick = { settingsViewModel.onChangePasswordClick() },
        onDarkThemeSwitch = { settingsViewModel.setDarkTheme(it) }
    )
}


@Composable
private fun SettingsContent(
    role: UserRole?,
    isDarkTheme: Boolean,
    preferences: Map<NotificationType, Boolean>,
    changePasswordState: ChangePasswordUiState,
    onNotificationToggle: (NotificationType, Boolean) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onChangePasswordClick: () -> Unit,
    onDarkThemeSwitch: (Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        item {
            SwitchRow(
                label = "Tema ${if (isDarkTheme) "scuro" else "chiaro"}",
                checked = isDarkTheme,
                onCheckedChange = onDarkThemeSwitch
            )
        }


        item {
            Text(
                "Notifiche",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        when (role) {
            UserRole.CLIENTE -> {
                item {
                    SwitchRow(
                        label = "Programmi",
                        checked = preferences[NotificationType.NewProgram] ?: true,
                        onCheckedChange = { onNotificationToggle(NotificationType.NewProgram, it) }
                    )
                }
                item {
                    SwitchRow(
                        label = "Risposta richiesta coaching",
                        checked = preferences[NotificationType.CoachingRequestResult] ?: true,
                        onCheckedChange = { onNotificationToggle(NotificationType.CoachingRequestResult, it) }
                    )
                }
            }
            UserRole.COACH -> {
                item {
                    SwitchRow(
                        label = "Richieste di coaching",
                        checked = preferences[NotificationType.CoachingRequest] ?: true,
                        onCheckedChange = { onNotificationToggle(NotificationType.CoachingRequest, it) }
                    )
                }
            }
            else -> {}
        }

        item {
            Text(
                "Sicurezza",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            if (changePasswordState.isLoading) {
                CircularProgressIndicator()
            } else {
                ElevatedCardComp {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextFieldComp(
                            value = changePasswordState.newPassword,
                            onValueChange = onNewPasswordChange,
                            label = "Nuova password",
                            error = changePasswordState.newPasswordError,
                            isPassword = true
                        )
                        TextFieldComp(
                            value = changePasswordState.confirmPassword,
                            onValueChange = onConfirmPasswordChange,
                            label = "Conferma password",
                            error = changePasswordState.confirmPasswordError,
                            isPassword = true
                        )
                        Column(modifier = Modifier.fillMaxSize()) {
                            Button(
                                onClick = onChangePasswordClick,
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Cambia password", color = Color.Black)
                            }

                            if (changePasswordState.saveSuccess) {
                                Text(
                                    "Salvata con successo!",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.align(
                                        Alignment.End
                                    )
                                )
                            }
                            if (changePasswordState.newPasswordError != null) {
                                Text(
                                    changePasswordState.newPasswordError,
                                    color = Red,
                                    modifier = Modifier.align(
                                        Alignment.End
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ElevatedCardComp {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
private fun PreviewSettingsScreen() {
    ProgettoWOCTheme {
        SettingsContent(
            role = UserRole.CLIENTE,
            preferences = mapOf(
                NotificationType.NewProgram to true,
                NotificationType.CoachingRequestResult to false
            ),
            onNotificationToggle = { _, _ -> },
            changePasswordState = ChangePasswordUiState(),
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            onChangePasswordClick = {},
            isDarkTheme = false,
            onDarkThemeSwitch = {}
        )
    }
}