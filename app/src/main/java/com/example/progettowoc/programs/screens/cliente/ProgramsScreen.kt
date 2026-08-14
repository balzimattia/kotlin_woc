package com.example.progettowoc.programs.screens.cliente

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.progettowoc.programs.viewmodels.ClienteProgramViewModel
import com.example.progettowoc.programs.data.Day
import com.example.progettowoc.programs.data.Exercise
import com.example.progettowoc.programs.data.ProgramSheet
import com.example.progettowoc.programs.data.Week
import com.example.progettowoc.ui.components.ElevatedCardComp
import com.example.progettowoc.ui.theme.ProgettoWOCTheme

@Composable
fun ProgramsScreen(
    clienteProgramViewModel: ClienteProgramViewModel = hiltViewModel(),
    onDayClick: () -> Unit
) {
    val currentProgramState by clienteProgramViewModel.currentProgramUiState.collectAsState()
    val latestProgramNum by clienteProgramViewModel.latestProgramNumState.collectAsState()


    if (currentProgramState.isLoadingScreen) {
        CircularProgressIndicator()
    } else {
        currentProgramState.currentProgram?.let { program ->
            ProgramsContent(
                isLoadingWeeks = currentProgramState.isLoadingWeeks,
                onProgramClick = { pNumber -> clienteProgramViewModel.getProgram(programNumber = pNumber) },
                currentProgramState = program,
                onDayClick = { weekNumber, dayNumber ->
                    clienteProgramViewModel.setCurrentDay(weekNumber = weekNumber, dayNumber = dayNumber)
                    onDayClick()
                },
                latestProgramNum = latestProgramNum,
            )
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nessun programma disponibile")
        }
    }
}


@Composable
private fun ProgramsContent(
    isLoadingWeeks: Boolean,
    onProgramClick: (Int) -> Unit,
    currentProgramState: ProgramSheet,
    onDayClick: (weekNumber: Int, dayNumber: Int) -> Unit,
    latestProgramNum: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {

        if (latestProgramNum > 0) {
            ActiveProgramDropdown(
                currentProgramState = currentProgramState,
                latestProgramNum = latestProgramNum,
                onProgramClick = onProgramClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoadingWeeks) {
                CircularProgressIndicator()
            } else {
                WeeksContent(
                    currentProgram = currentProgramState,
                    onDayClick = onDayClick
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActiveProgramDropdown(
    currentProgramState: ProgramSheet?,
    latestProgramNum: Int,
    onProgramClick: (Int) -> Unit
) {
    var isDropdownExpanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isDropdownExpanded,
        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = "Programma N. ${currentProgramState?.number ?: ""}",
            onValueChange = {},
            readOnly = true,
            label = { Text("Programmazione attiva") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true),
            shape = RoundedCornerShape(10.dp),
        )

        ExposedDropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false },
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 8.dp,
            containerColor = Color.White
        ) {
            for (i in 1..latestProgramNum) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Programma N. $i",
                            fontWeight = if (currentProgramState?.number == i) FontWeight.Bold else FontWeight.Normal,
                            color = Color.Black
                        )
                    },
                    onClick = {
                        onProgramClick(i)
                        isDropdownExpanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )

                if (i < latestProgramNum) {
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
private fun WeeksContent(
    currentProgram: ProgramSheet,
    onDayClick: (weekNumber: Int, dayNumber: Int) -> Unit
) {
    var expandedWeekNumber by rememberSaveable { mutableStateOf<Int?>(null) }

    LaunchedEffect(currentProgram.number) {
        expandedWeekNumber = null
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(top = 10.dp)
    ) {
        itemsIndexed(currentProgram.weeks) { index, week ->
            val isExpanded = expandedWeekNumber == index

            ElevatedCardComp(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    expandedWeekNumber = if (isExpanded) null else index
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Settimana ${week.number}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "settimana",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            week.days.forEach { day ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onDayClick(week.number, day.number)
                                        }
                                        .padding(vertical = 12.dp, horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Giorno ${day.number}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "Vedi esercizi",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewProgramsScreen() {
    ProgettoWOCTheme() {
        ProgramsContent(
            onProgramClick = {},
            latestProgramNum = 3,
            onDayClick = { _, _ -> },
            isLoadingWeeks = false,
            currentProgramState = ProgramSheet(
                number = 1,
                weeks = listOf(
                    Week(
                        number = 1,
                        days = listOf(
                            Day(
                                number = 1,
                                exercises = listOf(
                                    Exercise(
                                        name = "squat",
                                        sets = 3,
                                        reps = 5,
                                        rest = 120,
                                        weight = 120F,
                                        coachComment = "non spegnerti",
                                        clienteComment = ""
                                    ),
                                    Exercise(
                                        name = "panca",
                                        sets = 3,
                                        reps = 5,
                                        rest = 120,
                                        weight = 100F,
                                        coachComment = "",
                                        clienteComment = ""
                                    ),
                                ),
                                isCompleted = false
                            ),
                            Day(
                                number = 2,
                                exercises = listOf(
                                    Exercise(
                                        name = "squat",
                                        sets = 3,
                                        reps = 5,
                                        rest = 120,
                                        weight = 120F,
                                        coachComment = "non spegnerti",
                                        clienteComment = ""
                                    ),
                                    Exercise(
                                        name = "panca",
                                        sets = 3,
                                        reps = 5,
                                        rest = 120,
                                        weight = 100F,
                                        coachComment = "",
                                        clienteComment = ""
                                    ),
                                ),
                                isCompleted = false
                            ),
                            Day(
                                number = 3,
                                exercises = listOf(
                                    Exercise(
                                        name = "squat",
                                        sets = 3,
                                        reps = 5,
                                        rest = 120,
                                        weight = 120F,
                                        coachComment = "non spegnerti",
                                        clienteComment = ""
                                    ),
                                    Exercise(
                                        name = "panca",
                                        sets = 3,
                                        reps = 5,
                                        rest = 120,
                                        weight = 100F,
                                        coachComment = "",
                                        clienteComment = ""
                                    ),
                                ),
                                isCompleted = false
                            )
                        )
                    ),
                    Week(
                        number = 2,
                        days = listOf(
                            Day(
                                number = 1,
                                exercises = listOf(
                                    Exercise(
                                        name = "squat",
                                        sets = 3,
                                        reps = 5,
                                        rest = 120,
                                        weight = 120F,
                                        coachComment = "non spegnerti",
                                        clienteComment = ""
                                    ),
                                    Exercise(
                                        name = "panca",
                                        sets = 3,
                                        reps = 5,
                                        rest = 120,
                                        weight = 100F,
                                        coachComment = "",
                                        clienteComment = ""
                                    ),
                                ),
                                isCompleted = false
                            ),
                            Day(
                                number = 2,
                                exercises = listOf(
                                    Exercise(
                                        name = "squat",
                                        sets = 3,
                                        reps = 5,
                                        rest = 120,
                                        weight = 120F,
                                        coachComment = "non spegnerti",
                                        clienteComment = ""
                                    ),
                                    Exercise(
                                        name = "panca",
                                        sets = 3,
                                        reps = 5,
                                        rest = 120,
                                        weight = 100F,
                                        coachComment = "",
                                        clienteComment = ""
                                    ),
                                ),
                                isCompleted = false
                            ),
                            Day(
                                number = 3,
                                exercises = listOf(
                                    Exercise(
                                        name = "squat",
                                        sets = 3,
                                        reps = 5,
                                        rest = 120,
                                        weight = 120F,
                                        coachComment = "non spegnerti",
                                        clienteComment = ""
                                    ),
                                    Exercise(
                                        name = "panca",
                                        sets = 3,
                                        reps = 5,
                                        rest = 120,
                                        weight = 100F,
                                        coachComment = "",
                                        clienteComment = ""
                                    ),
                                ),
                                isCompleted = false
                            )
                        )
                    ),
                    Week(
                        number = 3,
                        days = listOf(
                            Day(
                                number = 1,
                                exercises = listOf(
                                    Exercise(
                                        name = "squat",
                                        sets = 3,
                                        reps = 5,
                                        rest = 120,
                                        weight = 120F,
                                        coachComment = "non spegnerti",
                                        clienteComment = ""
                                    ),
                                    Exercise(
                                        name = "panca",
                                        sets = 3,
                                        reps = 5,
                                        rest = 120,
                                        weight = 100F,
                                        coachComment = "",
                                        clienteComment = ""
                                    ),
                                ),
                                isCompleted = false
                            ),
                            Day(
                                number = 2,
                                exercises = listOf(
                                    Exercise(
                                        name = "squat",
                                        sets = 3,
                                        reps = 5,
                                        rest = 120,
                                        weight = 120F,
                                        coachComment = "non spegnerti",
                                        clienteComment = ""
                                    ),
                                    Exercise(
                                        name = "panca",
                                        sets = 3,
                                        reps = 5,
                                        rest = 120,
                                        weight = 100F,
                                        coachComment = "",
                                        clienteComment = ""
                                    ),
                                ),
                                isCompleted = false
                            ),
                            Day(
                                number = 3,
                                exercises = listOf(
                                    Exercise(
                                        name = "squat",
                                        sets = 3,
                                        reps = 5,
                                        rest = 120,
                                        weight = 120F,
                                        coachComment = "non spegnerti",
                                        clienteComment = ""
                                    ),
                                    Exercise(
                                        name = "panca",
                                        sets = 3,
                                        reps = 5,
                                        rest = 120,
                                        weight = 100F,
                                        coachComment = "",
                                        clienteComment = ""
                                    ),
                                ),
                                isCompleted = false
                            )
                        )
                    )
                )
            )
        )
    }
}