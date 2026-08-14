package com.example.progettowoc.programs.screens.coach

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.progettowoc.programs.viewmodels.CoachProgramViewModel
import com.example.progettowoc.programs.data.ProgramSheet
import com.example.progettowoc.ui.components.ElevatedCardComp
import com.example.progettowoc.ui.theme.LightGreen
import com.example.progettowoc.ui.theme.ProgettoWOCTheme
import com.example.progettowoc.users.data.User
import com.example.progettowoc.users.data.UserRole

@Composable
fun ClienteInfoEProgramsScreen(
    cliente: User,
    onProgramClick: (ProgramSheet) -> Unit,
    onNewProgramClick: () -> Unit,
    coachProgramViewModel: CoachProgramViewModel = hiltViewModel()
) {
    val listState by coachProgramViewModel.clienteProgramsList.collectAsState()
    val isLoading by coachProgramViewModel.isLoadingInfo.collectAsState()

    LaunchedEffect(Unit) {
        coachProgramViewModel.getClienteProgramsList(cliente.id)
    }

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        ClienteInfoEProgramsContent(
            cliente = cliente,
            programList = listState,
            onProgramClick = onProgramClick,
            onNewProgramClick = onNewProgramClick
        )
    }
}


@Composable
private fun ClienteInfoEProgramsContent(
    cliente: User,
    programList: List<ProgramSheet>,
    onProgramClick: (ProgramSheet) -> Unit,
    onNewProgramClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            contentPadding = PaddingValues(top = 20.dp, start = 15.dp, end = 15.dp, bottom = 80.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ElevatedCardComp {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            cliente.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            cliente.email,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            items(programList.reversed()) { program ->
                ElevatedCardComp(
                    onClick = { onProgramClick(program) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Programma N. ${program.number}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                            contentDescription = "vai al programma",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onNewProgramClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = LightGreen,
            contentColor = Color.Black
        ) {
            Icon(Icons.Default.Add, contentDescription = "Nuovo programma")
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun CIEPP() {
    ProgettoWOCTheme() {
        ClienteInfoEProgramsContent(
            onProgramClick = {},
            onNewProgramClick = {},
            cliente = User("", "prova@example.it", "prova prova", UserRole.CLIENTE),
            programList = listOf(
                ProgramSheet(number = 1, emptyList()),
                ProgramSheet(number = 2, emptyList()),
                ProgramSheet(number = 3, emptyList()),
                ProgramSheet(number = 4, emptyList()),
            )
        )
    }
}