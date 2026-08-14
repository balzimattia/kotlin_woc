package com.example.progettowoc.programs.screens.coach


import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.progettowoc.programs.uiStates.DayUiState
import com.example.progettowoc.programs.uiStates.EditProgramUiState
import com.example.progettowoc.programs.viewmodels.EditProgramViewModel
import com.example.progettowoc.programs.uiStates.ExerciseUiState
import com.example.progettowoc.programs.uiStates.WeekUiState
import com.example.progettowoc.programs.data.ProgramSheet
import com.example.progettowoc.ui.components.ElevatedCardComp
import com.example.progettowoc.ui.theme.Blue
import com.example.progettowoc.ui.theme.LightGreen
import com.example.progettowoc.ui.theme.ProgettoWOCTheme
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.progettowoc.R
import com.example.progettowoc.programs.viewmodels.CoachProgramViewModel
import com.example.progettowoc.ui.theme.Red
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.ui.unit.IntSize


@Composable
fun EditProgramScreen(
    clienteId: String,
    program: ProgramSheet?,
    coachProgramViewModel: CoachProgramViewModel,
    editProgramViewModel: EditProgramViewModel = hiltViewModel(),
    onSaveSuccess: () -> Unit
) {
    val uiState by editProgramViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(clienteId, program) {
        editProgramViewModel.init(clienteId, program?.number)
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            coachProgramViewModel.getClienteProgramsList(clienteId = clienteId, refresh = true)
            onSaveSuccess()
        }
    }


    //errore
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        editProgramViewModel.editErrorMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }


    val context = LocalContext.current
    // prende file
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { editProgramViewModel.loadFromFile(context, it) }
    }

    // crea file
    val exportFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    ) { uri ->
        uri?.let { editProgramViewModel.exportToFile(context, it) }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        EditProgramContent(
            currentProgram = uiState,
            onFilePickerClick = { filePickerLauncher.launch(arrayOf("*/*")) },
            onExportClick = { exportFileLauncher.launch(input = if (uiState.programNumber == 0) "nuovo_programma" else "programma_${uiState.programNumber}.xlsx") },
            onAddWeekClick = { editProgramViewModel.addWeek() },
            onRemoveWeek = { weekNum -> editProgramViewModel.removeWeek(weekNum) },
            onAddDay = { weekNum -> editProgramViewModel.addDay(weekNum) },
            onRemoveDay = { weekNum, dayNum -> editProgramViewModel.removeDay(weekNum, dayNum) },
            onAddExercise = { weekNum, dayNum ->
                editProgramViewModel.addExercise(
                    weekNum,
                    dayNum
                )
            },
            onRemoveExercise = { weekNum, dayNum, idx ->
                editProgramViewModel.removeExercise(
                    weekNum,
                    dayNum,
                    idx
                )
            },
            onUpdateExercise = { weekNum, dayNum, idx, ex ->
                editProgramViewModel.updateExercise(
                    weekNum,
                    dayNum,
                    idx,
                    ex
                )
            },
            onSaveClick = { editProgramViewModel.saveProgram(clienteId) }
        )
    }
}


@Composable
private fun EditProgramContent(
    modifier: Modifier = Modifier,
    currentProgram: EditProgramUiState,
    onFilePickerClick: () -> Unit,
    onExportClick: () -> Unit,
    onAddWeekClick: () -> Unit,
    onRemoveWeek: (Int) -> Unit,
    onAddDay: (Int) -> Unit,
    onRemoveDay: (Int, Int) -> Unit,
    onAddExercise: (Int, Int) -> Unit,
    onRemoveExercise: (Int, Int, Int) -> Unit,
    onUpdateExercise: (Int, Int, Int, ExerciseUiState) -> Unit,
    onSaveClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var showExample by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onPress = { focusManager.clearFocus() })
            }
    ) {

        if (currentProgram.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // programma
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 25.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (currentProgram.isNewProgram) "Nuovo Programma"
                            else "Programma N. ${currentProgram.programNumber}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ElevatedButton(
                                    onClick = onFilePickerClick,
                                    colors = ButtonDefaults.buttonColors(
                                        contentColor = Color.Black,
                                        containerColor = LightGreen
                                    )
                                ) {
                                    Text("Scegli file")
                                }

                                Text(
                                    "Esempio",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textDecoration = TextDecoration.Underline,
                                    modifier = Modifier.clickable(
                                        onClick = { showExample = true }
                                    )
                                )
                            }

                            ElevatedButton(
                                onClick = onExportClick,
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.Black,
                                    containerColor = LightGreen
                                )
                            ) {
                                Text("Scarica programma")
                            }
                        }
                    }
                }

                // Settimane
                currentProgram.weeks.forEach { week ->
                    item(key = "week_${week.number}") {
                        WeekCard(
                            week = week,
                            onRemoveWeek = { onRemoveWeek(week.number) },
                            onAddDay = { onAddDay(week.number) },
                            onRemoveDay = { dayNum -> onRemoveDay(week.number, dayNum) },
                            onAddExercise = { dayNum -> onAddExercise(week.number, dayNum) },
                            onRemoveExercise = { dayNum, idx ->
                                onRemoveExercise(
                                    week.number,
                                    dayNum,
                                    idx
                                )
                            },
                            onUpdateExercise = { dayNum, idx, ex ->
                                onUpdateExercise(
                                    week.number,
                                    dayNum,
                                    idx,
                                    ex
                                )
                            }
                        )
                    }
                }

                // Bottone aggiungi settimana
                item {
                    OutlinedButton(
                        onClick = onAddWeekClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Aggiungi Settimana")
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onSaveClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = LightGreen,
            contentColor = Color.Black
        ) {
            Icon(Icons.Default.Save, contentDescription = "Salva")
        }
    }

    if (showExample) {
        ExampleImageDialog(
            onDismiss = { showExample = false }
        )
    }
}


@Composable
private fun WeekCard(
    week: WeekUiState,
    onRemoveWeek: () -> Unit,
    onAddDay: () -> Unit,
    onRemoveDay: (dayNumber: Int) -> Unit,
    onAddExercise: (dayNumber: Int) -> Unit,
    onRemoveExercise: (dayNumber: Int, index: Int) -> Unit,
    onUpdateExercise: (dayNumber: Int, index: Int, ExerciseUiState) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(true) }

    ElevatedCardComp {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Settimana ${week.number}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Comprimi" else "Espandi",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onRemoveWeek) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Rimuovi settimana",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                week.days.forEach { day ->
                    DayCard(
                        day = day,
                        onRemoveDay = { onRemoveDay(day.number) },
                        onAddExercise = { onAddExercise(day.number) },
                        onRemoveExercise = { idx -> onRemoveExercise(day.number, idx) },
                        onUpdateExercise = { idx, ex -> onUpdateExercise(day.number, idx, ex) }
                    )
                }

                // Bottone aggiungi giorno
                TextButton(
                    onClick = onAddDay,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonColors(
                        contentColor = Blue,
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = Color.LightGray
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Aggiungi Giorno")
                }
            }
        }
    }
}


@Composable
private fun DayCard(
    day: DayUiState,
    onRemoveDay: () -> Unit,
    onAddExercise: () -> Unit,
    onRemoveExercise: (index: Int) -> Unit,
    onUpdateExercise: (index: Int, ExerciseUiState) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Giorno ${day.number}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Comprimi" else "Espandi",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onRemoveDay) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Rimuovi giorno",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Esercizi
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    day.exercises.forEachIndexed { index, exercise ->
                        ExerciseRow(
                            index = index,
                            exercise = exercise,
                            onRemove = { onRemoveExercise(index) },
                            onUpdate = { updated -> onUpdateExercise(index, updated) }
                        )
                    }

                    TextButton(
                        onClick = onAddExercise,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonColors(
                            contentColor = Blue,
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = Color.LightGray
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Aggiungi Esercizio")
                    }
                }
            }
        }
    }
}


@Composable
private fun ExerciseRow(
    index: Int,
    exercise: ExerciseUiState,
    onRemove: () -> Unit,
    onUpdate: (ExerciseUiState) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = exercise.name,
                    onValueChange = { onUpdate(exercise.copy(name = it)) },
                    label = { Text("Esercizio ${index + 1}") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Comprimi" else "Espandi",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Rimuovi esercizio",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Dettagli esercizio
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Serie / Rep / Recupero
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExerciseNumberField(
                            label = "Sets",
                            value = exercise.sets.toString(),
                            onValueChange = {
                                onUpdate(
                                    exercise.copy(
                                        sets = it.toIntOrNull() ?: 0
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ExerciseNumberField(
                            label = "Reps",
                            value = exercise.reps.toString(),
                            onValueChange = {
                                onUpdate(
                                    exercise.copy(
                                        reps = it.toIntOrNull() ?: 0
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExerciseNumberField(
                            label = "Rest(s)",
                            value = exercise.rest.toString(),
                            onValueChange = {
                                onUpdate(
                                    exercise.copy(
                                        rest = it.toIntOrNull() ?: 0
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ExerciseNumberField(
                            label = "Kg",
                            value = exercise.weight.toString(),
                            onValueChange = {
                                onUpdate(
                                    exercise.copy(
                                        weight = it.toFloatOrNull() ?: 0f
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Commento coach
                    OutlinedTextField(
                        value = exercise.coachComment,
                        onValueChange = { onUpdate(exercise.copy(coachComment = it)) },
                        label = { Text("Note coach") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )

                    //commmento cliente
                    OutlinedTextField(
                        readOnly = true,
                        value = exercise.clienteComment,
                        label = { Text("Note cliente") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        onValueChange = {}
                    )
                }
            }
        }
    }
}


@Composable
private fun ExerciseNumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var localValue by remember { mutableStateOf(value) }
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(value) {
        if (!isFocused) {
            localValue = value
        }
    }

    OutlinedTextField(
        value = localValue,
        onValueChange = {
            localValue = it
            onValueChange(it)
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier.onFocusChanged { focusState ->
            isFocused = focusState.isFocused
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}


@Composable
fun ExampleImageDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            ZoomableImage(
                painter = painterResource(R.drawable.example_sheet),
                modifier = Modifier.fillMaxSize()
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
            ) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
fun ZoomableImage(painter: Painter, modifier: Modifier = Modifier) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .onSizeChanged { containerSize = it }
            .pointerInput(Unit) {
                detectTransformGestures(panZoomLock = false) { _, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(1f, 5f)

                    val maxX = (containerSize.width * (newScale - 1f)) / 2f
                    val maxY = (containerSize.height * (newScale - 1f)) / 2f

                    offset = if (newScale <= 1f) {
                        Offset.Zero
                    } else {
                        Offset(
                            x = (offset.x + pan.x * newScale).coerceIn(-maxX, maxX),
                            y = (offset.y + pan.y * newScale).coerceIn(-maxY, maxY)
                        )
                    }
                    scale = newScale
                }
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = offset.x
                translationY = offset.y
            }
    )
}


@Preview(showBackground = true)
@Composable
private fun EditProgramPreview() {
    val fakeState = EditProgramUiState(
        isNewProgram = false,
        programNumber = 3,
        weeks = listOf(
            WeekUiState(
                number = 1,
                days = mutableListOf(
                    DayUiState(
                        number = 1,
                        exercises = mutableListOf(
                            ExerciseUiState(
                                name = "Squat",
                                sets = 4,
                                reps = 8,
                                rest = 120,
                                weight = 80f
                            ),
                            ExerciseUiState(
                                name = "Leg Press",
                                sets = 3,
                                reps = 12,
                                rest = 90,
                                weight = 120f
                            )
                        ),
                        isCompleted = false
                    ),
                )
            ),
            WeekUiState(number = 2)
        )
    )

    ProgettoWOCTheme() {
        EditProgramContent(
            currentProgram = fakeState,
            onAddWeekClick = {},
            onRemoveWeek = {},
            onAddDay = {},
            onRemoveDay = { _, _ -> },
            onAddExercise = { _, _ -> },
            onRemoveExercise = { _, _, _ -> },
            onUpdateExercise = { _, _, _, _ -> },
            onSaveClick = {},
            onFilePickerClick = {},
            onExportClick = {}
        )
    }
}