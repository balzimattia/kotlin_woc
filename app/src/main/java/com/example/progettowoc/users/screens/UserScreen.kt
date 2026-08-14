package com.example.progettowoc.users.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.progettowoc.R
import com.example.progettowoc.auth.viewmodels.AuthViewModel
import com.example.progettowoc.coaching.viewmodels.ClienteCoachingRelationViewModel
import com.example.progettowoc.ui.components.ElevatedCardComp
import com.example.progettowoc.ui.theme.ProgettoWOCTheme
import com.example.progettowoc.ui.theme.Red
import com.example.progettowoc.users.data.User
import com.example.progettowoc.users.data.UserRole
import io.github.jan.supabase.auth.status.SessionStatus

@Composable
fun UserScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    clienteCoachingRelationViewModel: ClienteCoachingRelationViewModel = hiltViewModel(),
    onSettingsClick: () -> Unit,
    onSearchCoachClick: () -> Unit,
    onClienteRequestsClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
    onOwnCoachClick: () -> Unit
) {
    val state by authViewModel.currentUser.collectAsState()
    val clienteOwnCoach by clienteCoachingRelationViewModel.clienteOwnCoachUiState.collectAsState()

    val isLoggingOut by authViewModel.isLoggingOut.collectAsState()
    val sessionStatus by authViewModel.sessionStatus.collectAsState()

    LaunchedEffect(sessionStatus) {
        if (sessionStatus is SessionStatus.NotAuthenticated) onLogoutSuccess()
    }

    LaunchedEffect(Unit) {
        clienteCoachingRelationViewModel.getClienteOwnCoach()
    }

    if (isLoggingOut) {
        CircularProgressIndicator()
        return
    }

    if (clienteOwnCoach.isLoadingOwnCoach) {
        CircularProgressIndicator()
    } else {
        UserContent(
            currentUser = state,
            ownCoachLoadError = clienteOwnCoach.ownCoachLoadError,
            clienteOwnCoach = clienteOwnCoach.ownCoach,
            onSettingsClick = onSettingsClick,
            onSearchCoachClick = onSearchCoachClick,
            onLogoutClick = { authViewModel.logout() },
            onClienteRequestsClick = onClienteRequestsClick,
            onOwnCoachClick = onOwnCoachClick
        )
    }
}


@Composable
private fun UserContent(
    currentUser: User?,
    ownCoachLoadError: Boolean,
    clienteOwnCoach: User?,
    onSettingsClick: () -> Unit,
    onSearchCoachClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onClienteRequestsClick: () -> Unit,
    onOwnCoachClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(35.dp, Alignment.CenterVertically)
    ) {

        ElevatedCardComp {
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    currentUser?.name ?: "",
                    fontWeight = FontWeight.Bold,
                    fontSize = 38.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    currentUser?.email ?: "",
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }


        if (currentUser?.role == UserRole.CLIENTE) {
            UserClienteContent(
                clienteOwnCoach = clienteOwnCoach,
                ownCoachLoadError = ownCoachLoadError,
                onOwnCoachClick = onOwnCoachClick,
                onSearchCoachClick = onSearchCoachClick
            )
        }
        else if(currentUser?.role == UserRole.COACH) {
            UserCoachContent(
                onClienteRequestsClick = onClienteRequestsClick
            )
        }


        ElevatedButton(
            onClick = onSettingsClick,
            colors = ButtonColors(
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                containerColor = MaterialTheme.colorScheme.onBackground,
                disabledContainerColor = Color.White,
                disabledContentColor = Color.White
            ),
            modifier = Modifier.padding(horizontal = 25.dp),
            contentPadding = PaddingValues(0.dp),
            elevation = ButtonDefaults.buttonElevation(8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(R.drawable.settings),
                    contentDescription = "impostazioni",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                )
                Text(
                    "Impostazioni", modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        if (currentUser != null) {
            ElevatedButton(
                modifier = Modifier
                    .height(40.dp)
                    .width(200.dp),
                onClick = onLogoutClick,
                colors = ButtonColors(
                    contentColor = Color.White,
                    containerColor = Color.Red,
                    disabledContainerColor = Color.White,
                    disabledContentColor = Color.White
                ),
                contentPadding = PaddingValues(0.dp),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Text("Logout", fontSize = 16.sp)
            }
        }
    }
}


@Composable
private fun UserClienteContent(
    clienteOwnCoach: User?,
    ownCoachLoadError: Boolean,
    onOwnCoachClick: () -> Unit,
    onSearchCoachClick: () -> Unit
) {
    ElevatedCardComp(
        onClick = if (clienteOwnCoach != null) { { onOwnCoachClick() } } else null
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Coach: ",
                fontSize = 26.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                when {
                    ownCoachLoadError -> "Errore durante il caricamento"
                    clienteOwnCoach != null -> clienteOwnCoach.name
                    else -> "Ancora nessun coach"
                },
                fontSize = 26.sp,
                color = if (ownCoachLoadError) Red else MaterialTheme.colorScheme.onBackground
            )
        }
    }

    if (clienteOwnCoach == null && !ownCoachLoadError) {
        ElevatedCardComp {
            ElevatedButton(
                onClick = onSearchCoachClick,
                colors = ButtonColors(
                    contentColor = Color.Black,
                    disabledContentColor = Color.LightGray,
                    containerColor = ButtonDefaults.buttonColors().containerColor,
                    disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor
                ),
                modifier = Modifier.fillMaxWidth(),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Text("Stato richiesta", fontSize = 16.sp)
            }
        }
    }
}


@Composable
private fun UserCoachContent(
    onClienteRequestsClick: () -> Unit
) {
    ElevatedCardComp {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ElevatedButton(
                onClick = onClienteRequestsClick,
                colors = ButtonColors(
                    contentColor = Color.Black,
                    disabledContentColor = Color.LightGray,
                    containerColor = ButtonDefaults.buttonColors().containerColor,
                    disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(10.dp)
            ) {
                Text("Richieste di coaching clienti")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewUserScreen() {
    ProgettoWOCTheme() {
        UserContent(
            currentUser = User("", "prova@example.it", "prova prova", UserRole.CLIENTE),
            clienteOwnCoach = User("", "prova@example.it", "prova prova", UserRole.COACH),
            ownCoachLoadError = false,
            onSettingsClick = {},
            onSearchCoachClick = {},
            onClienteRequestsClick = {},
            onOwnCoachClick = {},
            onLogoutClick = {},
        )
    }
}