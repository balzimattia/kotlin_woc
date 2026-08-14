package com.example.progettowoc.coaching.screens.coach

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.progettowoc.coaching.viewmodels.ClienteCoachingRequestViewModel
import com.example.progettowoc.coaching.viewmodels.CoachCoachingRequestViewModel
import com.example.progettowoc.ui.components.ElevatedCardComp
import com.example.progettowoc.ui.theme.ProgettoWOCTheme
import com.example.progettowoc.users.data.User
import com.example.progettowoc.users.data.UserRole

@Composable
fun RequestsListScreen(
    coachingRequestViewModel: CoachCoachingRequestViewModel = hiltViewModel(),
    onSelectedUser: (User) -> Unit
) {
    val requestsList by coachingRequestViewModel.coachingRequestsList.collectAsState()
    val isLoadingList by coachingRequestViewModel.isLoadingRequestsList.collectAsState()

    if (isLoadingList) {
        CircularProgressIndicator()
    } else {
        RequestsListContent(
            list = requestsList,
            onSelectedUser = onSelectedUser
        )
    }
}


@Composable
private fun RequestsListContent(
    list: List<User>,
    onSelectedUser: (User) -> Unit
) {
    if (list.isEmpty()) {
        Column(
            modifier = Modifier.padding(top = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Nessuna richiesta disponibile",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(list) { user ->
                ElevatedCardComp(
                    onClick = { onSelectedUser(user) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Nome: ${user.name}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Email: ${user.email}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "vai all'esercizio",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun RequestsListPreview() {
    ProgettoWOCTheme() {
        RequestsListContent(
            list = listOf(
                User("", "prova", "prova", UserRole.CLIENTE),
                User("", "prova", "prova", UserRole.CLIENTE),
                User("", "prova", "prova", UserRole.CLIENTE),
                User("", "prova", "prova", UserRole.CLIENTE),
                User("", "prova", "prova", UserRole.CLIENTE)
            ),
            onSelectedUser = {}
        )
    }
}