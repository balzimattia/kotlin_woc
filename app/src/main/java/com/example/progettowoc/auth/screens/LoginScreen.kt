package com.example.progettowoc.auth.screens


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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progettowoc.auth.viewmodels.LoginViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.progettowoc.auth.uiStates.LoginUiState
import com.example.progettowoc.ui.components.BackArrowButtonComp
import com.example.progettowoc.ui.components.TextFieldComp
import com.example.progettowoc.ui.theme.LightGreen
import com.example.progettowoc.ui.theme.ProgettoWOCTheme

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by loginViewModel.uiState.collectAsState()

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) {
            onLoginSuccess()
        }
    }

    if(state.isLoading || state.isLoggedIn) {
        CircularProgressIndicator()
    } else {
        LoginContent(
            state = state,
            onEmailChange = { loginViewModel.onEmailChange(it) },
            onPasswordChange = { loginViewModel.onPasswordChange(it) },
            onLoginClick = { loginViewModel.onLoginClick() },
            onBackClick = onBackClick
        )
    }
}


@Composable
private fun LoginContent(
    state: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
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
            modifier = Modifier.fillMaxSize().align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterVertically)
        ) {

            Text(
                "ACCEDI",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            state.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 18.sp
                )
            }

            TextFieldComp(
                label = "Email",
                value = state.email,
                onValueChange = onEmailChange,
                error = state.errorMessage,
                showErrorText = false
            )

            TextFieldComp(
                label = "Password",
                value = state.password,
                onValueChange = onPasswordChange,
                error = state.errorMessage,
                showErrorText = false,
                isPassword = true
            )

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            OutlinedButton(
                onClick = onLoginClick,
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier.height(40.dp).width(200.dp).semantics { testTag = "login_button" },
                border = BorderStroke(2.dp, LightGreen),
                interactionSource = interactionSource,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isPressed) LightGreen else Color.Transparent
                )
            ) {
                Text("LOGIN", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}



@Composable
@Preview(showBackground = true)
private fun LoginScreenPreview() {
    ProgettoWOCTheme() {
        LoginContent(
            state = LoginUiState(email = "", password = ""),
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onBackClick = {}
        )
    }
}