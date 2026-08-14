package com.example.progettowoc.coaching.screens.cliente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
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
import com.example.progettowoc.ui.theme.LightGreen
import com.example.progettowoc.ui.theme.ProgettoWOCTheme
import com.example.progettowoc.ui.theme.Red
import com.example.progettowoc.users.data.User
import com.example.progettowoc.users.data.UserRole
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CoachingRequestScreen(
    coach: User,
    coachingRequestViewModel: ClienteCoachingRequestViewModel = hiltViewModel(),
    onInviaClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        coachingRequestViewModel.requestErrorMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        CoachRequestContent(
            coach = coach,
            onInviaClick = {
                coachingRequestViewModel.addRequest(coach.id)
                onInviaClick()
            }
        )
    }
}


@Composable
private fun CoachRequestContent(
    modifier: Modifier = Modifier,
    coach: User,
    onInviaClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically)
    ) {

        Text(coach.role.toRoleString.uppercase(), fontWeight = FontWeight.Bold)
        Text(coach.name, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(coach.email, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)

        ElevatedButton(
            onClick = onInviaClick,
            elevation = ButtonDefaults.buttonElevation(8.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Black,
                containerColor = LightGreen
            )
        ) {
            Text("INVIA RICHIESTA")
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun CoachRequestPreview() {
    ProgettoWOCTheme() {
        CoachRequestContent(
            coach = User(
                id = "",
                name = "prova coach",
                email = "prova@example.it",
                role = UserRole.COACH
            ),
            onInviaClick = {}
        )
    }
}