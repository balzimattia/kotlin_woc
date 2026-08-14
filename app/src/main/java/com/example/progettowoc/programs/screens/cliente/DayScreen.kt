package com.example.progettowoc.programs.screens.cliente

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progettowoc.programs.viewmodels.ClienteProgramViewModel
import com.example.progettowoc.programs.data.Day
import com.example.progettowoc.programs.data.Exercise
import com.example.progettowoc.ui.components.ElevatedCardComp
import com.example.progettowoc.ui.theme.GreenAcceso
import com.example.progettowoc.ui.theme.LightGreen
import com.example.progettowoc.ui.theme.ProgettoWOCTheme

@Composable
fun DayScreen(
    clienteProgramViewModel: ClienteProgramViewModel,
    onExerciseClick: (exerciseIndex: Int) -> Unit
) {
    val currentProgramState by clienteProgramViewModel.currentProgramUiState.collectAsState()
    val currentProgram = currentProgramState.currentProgram
    val weekNumber = currentProgramState.currentWeekNumber
    val dayNumber = currentProgramState.currentDayNumber
    val isDayCompletedLoading by clienteProgramViewModel.isDayCompletedLoading.collectAsState()


    // questo e gli if else servono nel caso si arrivi dalla home
    LaunchedEffect(Unit) {
        if (currentProgram == null) clienteProgramViewModel.loadInitialData()
    }

    if (currentProgramState.isLoadingScreen) {
        CircularProgressIndicator()
    } else {
        val day =
            if (weekNumber != null && dayNumber != null) {
                clienteProgramViewModel.getDay(weekNumber = weekNumber, dayNumber = dayNumber)
            } else {
                clienteProgramViewModel.getNextDay()
            }

        if (day == null) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text("Giorno non disponibile", fontSize = 26.sp, color = MaterialTheme.colorScheme.onBackground)
            }
        } else {
            DayContent(
                day = day,
                isDayCompletedLoading = isDayCompletedLoading,
                onExerciseClick = onExerciseClick,
                onCompleteDay = {
                    if (weekNumber != null && dayNumber != null) {
                        clienteProgramViewModel.completeDay(weekNumber, dayNumber)
                    }
                }
            )
        }
    }
}


@Composable
private fun DayContent(
    day: Day,
    isDayCompletedLoading: Boolean,
    onExerciseClick: (exerciseIndex: Int) -> Unit,
    onCompleteDay: () -> Unit
) {
    var selectedIndex by rememberSaveable { mutableStateOf<Int?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { selectedIndex = null }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Spacer(Modifier.height(15.dp))
                Button(
                    onClick = onCompleteDay,
                    enabled = !day.isCompleted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp, horizontal = 30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (day.isCompleted) ButtonDefaults.buttonColors().disabledContainerColor else LightGreen
                    )
                ) {
                    if (isDayCompletedLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(25.dp))
                    } else {
                        Icon(
                            imageVector = if (day.isCompleted) Icons.Default.Check else Icons.Default.CheckCircle,
                            contentDescription = null,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (day.isCompleted) "Giorno completato" else "Segna come completato",
                            color = Color.Black
                        )
                    }
                }
            }


            itemsIndexed(day.exercises) { index, exercise ->
                val isSelected = selectedIndex == index

                ElevatedCardComp(
                    isSelected = isSelected,
                    onClick = {
                        selectedIndex = if (isSelected) null else index
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "${exercise.name} - ${exercise.sets}x${exercise.reps} rest: ${exercise.rest}s peso: ${exercise.weight}kg",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                            contentDescription = "vai all'esercizio",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = selectedIndex != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            FloatingActionButton(
                onClick = { selectedIndex?.let { onExerciseClick(it) } },
                modifier = Modifier.padding(16.dp),
                containerColor = GreenAcceso
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Avvia esercizio",
                    tint = Color.Black
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun DayPreview() {
    ProgettoWOCTheme() {
        DayContent(
            onExerciseClick = {},
            onCompleteDay = {},
            isDayCompletedLoading = true,
            day = Day(
                number = 1,
                isCompleted = true,
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
                )
            )
        )
    }
}