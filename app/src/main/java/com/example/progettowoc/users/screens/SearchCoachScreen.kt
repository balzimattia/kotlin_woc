package com.example.progettowoc.users.screens

import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.progettowoc.coaching.data.RequestStatus
import com.example.progettowoc.coaching.viewmodels.ClienteCoachingRequestViewModel
import com.example.progettowoc.ui.components.ElevatedCardComp
import com.example.progettowoc.ui.theme.ProgettoWOCTheme
import com.example.progettowoc.users.data.User
import com.example.progettowoc.users.uiStates.SearchCoachUiState
import com.example.progettowoc.users.viewmodels.SearchCoachViewModel

@Composable
fun SearchCoachScreen(
    searchCoachViewModel: SearchCoachViewModel = hiltViewModel(),
    coachingRequestViewModel: ClienteCoachingRequestViewModel = hiltViewModel(),
    onSelectedCoach: (User) -> Unit
) {
    val state by searchCoachViewModel.searchCoachUiState.collectAsState()
    val requestStatus by coachingRequestViewModel.requestStatus.collectAsState()
    val isLoading by coachingRequestViewModel.isLoadingRequestStatus.collectAsState()

    LaunchedEffect(Unit) {
        coachingRequestViewModel.getPendingRequest()
    }

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        SearchCoachContent(
            state = state,
            requestStatus = requestStatus,
            onSearchChange = { searchCoachViewModel.onSearchChange(it) },
            onSearchClick = { searchCoachViewModel.searchCoaches() },
            onSelectedCoach = onSelectedCoach,
            onCancelRequest = { coachingRequestViewModel.deletePendingRequest() }
        )
    }
}


@Composable
private fun SearchCoachContent(
    state: SearchCoachUiState,
    requestStatus: RequestStatus?,
    onSearchChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onSelectedCoach: (User) -> Unit,
    onCancelRequest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Log.e("status", requestStatus?.toStatusString ?: "null")
        when (requestStatus) {
            RequestStatus.PENDING -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ElevatedCardComp {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Hai già una richiesta in corso",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            ElevatedButton(
                                onClick = onCancelRequest,
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Annulla richiesta")
                            }
                        }
                    }
                }
            }

            RequestStatus.REJECTED, null -> {
                OutlinedTextField(
                    value = state.search,
                    onValueChange = onSearchChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ricerca coach", color = Color.Black) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black)
                    },
                    trailingIcon = {
                        if (state.search.isNotBlank()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Cancella",
                                    tint = Color.Black
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearchClick() }),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(15.dp)
                    ) {
                        items(state.coachesList) { coach ->
                            ElevatedCardComp(
                                onClick = { onSelectedCoach(coach) }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            coach.name,
                                            fontSize = 24.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            coach.email,
                                            fontSize = 20.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "visualizza coach",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            RequestStatus.ACCEPTED -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Hai già un coach")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun SearchCoachPreview() {
    ProgettoWOCTheme() {
        SearchCoachContent(
            state = SearchCoachUiState(),
            requestStatus = null,
            onSearchClick = {},
            onSearchChange = {},
            onSelectedCoach = {},
            onCancelRequest = {}
        )
    }
}