package com.example.progettowoc.coaching.screens.cliente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progettowoc.coaching.viewmodels.ClienteCoachingRelationViewModel
import com.example.progettowoc.ui.components.ElevatedCardComp
import com.example.progettowoc.ui.theme.LightGreen
import com.example.progettowoc.ui.theme.ProgettoWOCTheme
import com.example.progettowoc.ui.theme.Red
import com.example.progettowoc.users.data.User
import com.example.progettowoc.users.data.UserRole
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun OwnCoachRelationScreen(
    coachingRelationViewModel: ClienteCoachingRelationViewModel,
    onRemoveRelationClick: () -> Unit
) {
    val ownCoachState by coachingRelationViewModel.clienteOwnCoachUiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        coachingRelationViewModel.ownCoachErrorMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(ownCoachState.ownCoach, ownCoachState.isLoadingOwnCoach) {
        if (!ownCoachState.isLoadingOwnCoach && ownCoachState.ownCoach == null) {
            onRemoveRelationClick()
        }
    }

    ownCoachState.ownCoach?.let {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            OwnCoachRelationContent(
                ownCoach = it,
                onRemoveRelationClick = { coachingRelationViewModel.removeCoachingRelation() }
            )
        }
    }
}


@Composable
private fun OwnCoachRelationContent(
    modifier: Modifier = Modifier,
    ownCoach: User,
    onRemoveRelationClick: () -> Unit
) {

    var showDialog by remember { mutableStateOf(false) }


    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ElevatedCardComp {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    ownCoach.name,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    ownCoach.email,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(15.dp))
                ElevatedButton(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Red,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier
                        .height(50.dp)
                ) {
                    Text("Termina coaching")
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Termina coaching") },
                titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                text = { Text("Sei sicuro di voler terminare il coaching con ${ownCoach.name}?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            onRemoveRelationClick()
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = LightGreen
                        )
                    ) {
                        Text("Conferma", color = Color.Black)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Red
                        )
                    ) {
                        Text("Annulla", color = Color.Black)
                    }
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun OwnCoachRelationPreview() {
    ProgettoWOCTheme() {
        OwnCoachRelationContent(
            ownCoach = User("", "prova@example.it", "prova coach", UserRole.COACH),
            onRemoveRelationClick = {}
        )
    }
}