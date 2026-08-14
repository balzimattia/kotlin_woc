package com.example.progettowoc.coaching.screens.coach

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.progettowoc.coaching.viewmodels.ClienteCoachingRequestViewModel
import com.example.progettowoc.coaching.viewmodels.CoachCoachingRequestViewModel
import com.example.progettowoc.ui.theme.LightGreen
import com.example.progettowoc.ui.theme.ProgettoWOCTheme
import com.example.progettowoc.ui.theme.Red
import com.example.progettowoc.users.data.User
import com.example.progettowoc.users.data.UserRole
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ClienteRequestScreen(
    cliente: User,
    coachingRequestViewModel: CoachCoachingRequestViewModel,
    onButtonClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        coachingRequestViewModel.errorRequestMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        ClientRequestContent(
            cliente = cliente,
            onAcceptClick = {
                coachingRequestViewModel.updateRequest(isAccepted = true, clienteId = cliente.id)
                onButtonClick()
            },
            onRejectClick = {
                coachingRequestViewModel.updateRequest(isAccepted = false, clienteId = cliente.id)
                onButtonClick()
            }
        )
    }
}


@Composable
private fun ClientRequestContent(
    modifier: Modifier = Modifier,
    cliente: User,
    onAcceptClick: () -> Unit,
    onRejectClick: () -> Unit
) {

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically)
    ) {
        Text(cliente.name, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(cliente.email, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)

        Button(
            onClick = onAcceptClick,
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Black,
                containerColor = LightGreen
            )
        ) {
            Text("Accetta")
        }

        Button(
            onClick = onRejectClick,
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Black,
                containerColor = Red
            )
        ) {
            Text("Rifiuta")
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ClientRequestPreview() {
    ProgettoWOCTheme() {
        ClientRequestContent(
            cliente = User("", "prova@example.it", "prova prova", UserRole.CLIENTE),
            onAcceptClick = {},
            onRejectClick = {}
        )
    }
}