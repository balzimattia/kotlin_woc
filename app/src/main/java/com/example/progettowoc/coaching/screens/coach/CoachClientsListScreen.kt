package com.example.progettowoc.coaching.screens.coach

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.progettowoc.ui.components.ElevatedCardComp
import com.example.progettowoc.ui.theme.LightBlue
import com.example.progettowoc.ui.theme.ProgettoWOCTheme
import com.example.progettowoc.users.data.User
import com.example.progettowoc.users.data.UserRole
import com.example.progettowoc.coaching.viewmodels.ClientiListViewModel

@Composable
fun CoachClientsListScreen(
    clientiListViewModel: ClientiListViewModel = hiltViewModel(),
    onClienteClick: (User) -> Unit
) {
    val clientiList by clientiListViewModel.clientiList.collectAsState()
    val isLoading by clientiListViewModel.isLoadingClientiList.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        clientiListViewModel.errorMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }


    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            CoachClientsListContent(
                clientiList = clientiList,
                onClienteClick = onClienteClick,
                onRemoveRelation = { clientiListViewModel.removeRelationWithCliente(it) }
            )
        }
    }
}


@Composable
private fun CoachClientsListContent(
    clientiList: List<User>,
    onClienteClick: (User) -> Unit,
    onRemoveRelation: (User) -> Unit
) {
    var selectedCliente by remember { mutableStateOf<User?>(null) }

    val focusManager = LocalFocusManager.current
    var search by remember { mutableStateOf("") }
    val filteredList = remember(search, clientiList) {
        if (search.isBlank()) clientiList
        else clientiList.filter {
            it.name.contains(search, ignoreCase = true)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { focusManager.clearFocus() }
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Cerca cliente") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LightBlue,
                    unfocusedBorderColor = LightBlue,
                    focusedLabelColor = LightBlue,
                    unfocusedLabelColor = LightBlue,
                    focusedLeadingIconColor = LightBlue,
                    unfocusedLeadingIconColor = LightBlue,
                    cursorColor = LightBlue,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp, vertical = 8.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(15.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList) { cliente ->
                    val isSelected = selectedCliente == cliente

                    ElevatedCardComp(
                        isSelected = isSelected,
                        onClick = {
                            if (selectedCliente != null) selectedCliente = null
                            else onClienteClick(cliente)
                        },
                        onLongClick = { selectedCliente = cliente }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    cliente.name,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    cliente.email,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                contentDescription = "visualizza cliente",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight
                            )
                        }
                    }
                }
            }
        }

        // bottone elimina relazione con il cliente
        AnimatedVisibility(
            visible = selectedCliente != null,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            ElevatedButton(
                onClick = {
                    selectedCliente?.let { onRemoveRelation(it) }
                    selectedCliente = null
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Rimuovi cliente dal coaching")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun CCLP() {
    ProgettoWOCTheme() {
        CoachClientsListContent(
            onClienteClick = {},
            clientiList = listOf(
                User("", "prova@email.it", "prova prova", UserRole.CLIENTE),
                User("", "prova@email.it", "prova prova", UserRole.CLIENTE),
                User("", "prova@email.it", "prova prova", UserRole.CLIENTE)
            ),
            onRemoveRelation = {}
        )
    }
}