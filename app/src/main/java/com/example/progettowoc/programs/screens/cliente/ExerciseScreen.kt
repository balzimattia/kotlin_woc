package com.example.progettowoc.programs.screens.cliente

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progettowoc.programs.viewmodels.ClienteProgramViewModel
import com.example.progettowoc.programs.data.Exercise
import com.example.progettowoc.ui.components.ElevatedCardComp
import com.example.progettowoc.ui.components.TimerComp
import com.example.progettowoc.ui.components.TimerDisplay
import com.example.progettowoc.ui.theme.LightGreen
import com.example.progettowoc.ui.theme.ProgettoWOCTheme
import com.example.progettowoc.ui.theme.Red


@Composable
fun ExerciseScreen(
    exercise: Exercise,
    exerciseIndex: Int,
    clienteProgramViewModel: ClienteProgramViewModel
) {
    val currentProgramState by clienteProgramViewModel.currentProgramUiState.collectAsState()
    val weekNumber = currentProgramState.currentWeekNumber
    val dayNumber = currentProgramState.currentDayNumber


    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        clienteProgramViewModel.exerciseErrorMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }



    if (weekNumber != null && dayNumber != null) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            ExerciseContent(
                exercise = exercise,
                onWeightUpdate = { newWeight ->
                    clienteProgramViewModel.changeWeight(
                        weekNumber = weekNumber,
                        dayNumber = dayNumber,
                        exerciseIndex = exerciseIndex,
                        newWeight = newWeight
                    )
                },
                onCommentUpdate = { comment ->
                    clienteProgramViewModel.updateClienteComment(
                        weekNumber,
                        dayNumber,
                        exerciseIndex,
                        comment
                    )
                }
            )
        }
    }
}


@Composable
private fun ExerciseContent(
    modifier: Modifier = Modifier,
    exercise: Exercise,
    onWeightUpdate: (Float) -> Unit,
    onCommentUpdate: (String) -> Unit
) {
    var setsRemaining by rememberSaveable(exercise.sets) { mutableIntStateOf(exercise.sets) }
    var isTimerRunning by rememberSaveable { mutableStateOf(false) }
    var timerKey by rememberSaveable { mutableIntStateOf(0) }
    val isCompleted = setsRemaining <= 0
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .pointerInput(Unit) {
                detectTapGestures(onPress = { focusManager.clearFocus() })
            }
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        Text(
            text = exercise.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        ExerciseInfoCard(
            exercise = exercise,
            onWeightUpdate = onWeightUpdate
        )

        SetsRemainingSection(
            setsRemaining = setsRemaining,
            isCompleted = isCompleted
        )

        TimerSection(
            restSeconds = exercise.rest,
            timerKey = timerKey,
            isRunning = isTimerRunning,
            isCompleted = isCompleted,
            onToggle = { isTimerRunning = !isTimerRunning },
            onFinish = {
                isTimerRunning = false
                setsRemaining--
                timerKey++ // resetta il timer
            }
        )

        if (exercise.coachComment.isNotBlank()) {
            CoachCommentSection(comment = exercise.coachComment)
        }

        ClienteCommentSection(
            comment = exercise.clienteComment,
            onSaveComment = onCommentUpdate
        )
    }
}


@Composable
private fun ExerciseInfoCard(
    exercise: Exercise,
    onWeightUpdate: (Float) -> Unit
) {
    ElevatedCardComp(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            InfoSquare(label = "Serie", value = exercise.sets.toString())
            InfoSquare(label = "Reps", value = exercise.reps.toString())
            InfoSquare(label = "Rec.", value = "${exercise.rest}s")
            WeightSquare(
                weight = exercise.weight,
                onWeightUpdate = onWeightUpdate
            )
        }
    }
}


@Composable
private fun InfoSquare(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
private fun WeightSquare(
    weight: Float,
    onWeightUpdate: (Float) -> Unit
) {
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var inputValue by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isEditing) {
            OutlinedTextField(
                value = inputValue,
                onValueChange = { inputValue = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val parsed = inputValue.toFloatOrNull()
                        if (parsed != null) onWeightUpdate(parsed)
                        isEditing = false
                    }
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        val parsed = inputValue.toFloatOrNull()
                        if (parsed != null) onWeightUpdate(parsed)
                        isEditing = false
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Conferma")
                    }
                },
                modifier = Modifier
                    .width(100.dp)
                    .focusRequester(focusRequester),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            LaunchedEffect(Unit) { focusRequester.requestFocus() }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        inputValue = ""
                        isEditing = true
                    }
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (weight == 0f) "--" else "${weight}kg",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Modifica peso",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Peso",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
private fun SetsRemainingSection(
    setsRemaining: Int,
    isCompleted: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Serie rimanenti:",
            style = MaterialTheme.typography.titleMedium
        )

        AnimatedVisibility(
            visible = isCompleted,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Surface(
                color = LightGreen,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "✓ Completato",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        AnimatedVisibility(
            visible = !isCompleted,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(LightGreen)
            ) {
                Text(
                    text = setsRemaining.toString(),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}


@Composable
private fun TimerSection(
    restSeconds: Int,
    timerKey: Int,
    isRunning: Boolean,
    isCompleted: Boolean,
    onToggle: () -> Unit,
    onFinish: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        TimerComp(
            key = timerKey,
            secondsRemaining = restSeconds,
            isRunning = isRunning,
            onFinish = onFinish
        ) { formattedTime, remaining ->
            TimerDisplay(
                formattedTime = formattedTime,
                remaining = remaining,
                total = restSeconds
            )
        }

        Button(
            onClick = onToggle,
            enabled = !isCompleted,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonColors(
                containerColor = if (!isRunning) LightGreen else Red,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor,
                disabledContentColor = ButtonDefaults.buttonColors().disabledContentColor
            )
        ) {
            Text(
                text = if (isRunning) "Pausa" else "Avvia Recupero",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Black
            )
        }
    }
}


@Composable
private fun CoachCommentSection(comment: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Note del coach",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = comment,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
private fun ClienteCommentSection(
    comment: String,
    onSaveComment: (String) -> Unit
) {
    var localComment by rememberSaveable(comment) { mutableStateOf(comment) }
    val focusManager = LocalFocusManager.current

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onPress = { focusManager.clearFocus() })
                },
        ) {
            Text(
                text = "Note mie",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = localComment,
                onValueChange = { localComment = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Aggiungi una nota...") },
                minLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { onSaveComment(localComment) },
                modifier = Modifier.align(Alignment.End),
                enabled = localComment != comment,
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Black,
                    disabledContentColor = ButtonDefaults.buttonColors().disabledContentColor
                )
            ) {
                Text("Salva")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ExercisePreview() {
    ProgettoWOCTheme() {
        ExerciseContent(
            exercise = Exercise(
                name = "Squat",
                sets = 4,
                reps = 8,
                rest = 11,
                weight = 80f,
                coachComment = "Scendi fino a parallelo, schiena dritta.",
                clienteComment = ""
            ),
            onWeightUpdate = {},
            onCommentUpdate = {}
        )
    }
}