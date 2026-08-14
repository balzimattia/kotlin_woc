package com.example.progettowoc.auth.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.progettowoc.auth.uiStates.RegisterUiState
import com.example.progettowoc.auth.viewmodels.RegisterViewModel
import com.example.progettowoc.ui.components.BackArrowButtonComp
import com.example.progettowoc.ui.components.TextFieldComp
import com.example.progettowoc.ui.theme.LightGreen
import com.example.progettowoc.ui.theme.ProgettoWOCTheme
import com.example.progettowoc.ui.theme.Red
import com.example.progettowoc.users.data.UserRole

@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val state by registerViewModel.uiState.collectAsState()

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) {
            onRegisterSuccess()
        }
    }

    if (state.isLoading || state.isLoggedIn) {
        CircularProgressIndicator()
    } else {
        RegisterContent(
            state = state,
            onNameChange = { registerViewModel.onNameChange(it) },
            onEmailChange = { registerViewModel.onEmailChange(it) },
            onRoleChange = { registerViewModel.onRoleChange(it) },
            onPasswordChange = { registerViewModel.onPasswordChange(it) },
            onConfirmPasswordChange = { registerViewModel.onConfirmPasswordChange(it) },
            onRegisterClick = { registerViewModel.onRegisterClick() },
            onBackClick = onBackClick
        )
    }
}


@Composable
private fun RegisterContent(
    state: RegisterUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(20.dp)
        ) {
            BackArrowButtonComp(onBackClick = onBackClick)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterVertically)
        ) {

            Text(
                "INSERISCI I DATI:",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            TextFieldComp(
                label = "Nome e cognome",
                value = state.name,
                onValueChange = onNameChange,
                error = state.nameError
            )

            TextFieldComp(
                label = "Email",
                value = state.email,
                onValueChange = onEmailChange,
                error = state.emailError
            )

            RoleDropdownMenu(
                onValueChange = onRoleChange,
                selectedRole = state.role,
                error = state.roleError
            )

            TextFieldComp(
                label = "Password",
                value = state.password,
                onValueChange = onPasswordChange,
                error = state.passwordError,
                isPassword = true
            )

            TextFieldComp(
                label = "Conferma password",
                value = state.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                error = state.confirmPasswordError,
                isPassword = true
            )

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()


            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedButton(
                    onClick = onRegisterClick, //register
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .height(40.dp)
                        .width(200.dp)
                        .semantics { testTag = "Registrati_button" },
                    border = BorderStroke(2.dp, LightGreen),
                    interactionSource = interactionSource,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isPressed) LightGreen else Color.Transparent
                    )
                ) {
                    Text(
                        "REGISTRATI",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                state.generalError?.let { Text(it, color = Red) }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoleDropdownMenu(
    onValueChange: (UserRole) -> Unit,
    selectedRole: UserRole?,
    error: String?
) {
    val options = UserRole.entries
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedRole?.toRoleString ?: "",
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    "Ruolo",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            trailingIcon = {
                val rotation by animateFloatAsState(
                    targetValue = if (expanded) 180f else 0f
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotation)
                )
            },
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
            modifier = Modifier.menuAnchor().semantics { testTag = "Ruolo" },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground
            ),
            shape = RoundedCornerShape(12.dp),
            isError = error != null,
            supportingText = { error?.let { Text(it) } }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 8.dp,
            containerColor = Color.White
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option.toRoleString, color = Color.Black) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    },
                    modifier = Modifier.semantics { testTag = "Ruolo_${option.toRoleString}" }
                )

                if (index < options.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
private fun RegisterScreenPreview() {
    ProgettoWOCTheme {
        RegisterContent(
            state = RegisterUiState(),
            {},
            {},
            {},
            {},
            {},
            {},
            {}
        )
    }
}