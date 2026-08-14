package com.example.progettowoc.notifications.screens

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.progettowoc.auth.viewmodels.AuthViewModel
import com.example.progettowoc.notifications.NotificationViewModel
import com.example.progettowoc.users.data.UserRole
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.progettowoc.notifications.NotificationType
import com.example.progettowoc.notifications.data.Notification
import com.example.progettowoc.ui.components.ElevatedCardComp
import com.example.progettowoc.ui.theme.ProgettoWOCTheme
import androidx.compose.foundation.lazy.items


@Composable
fun NotificationScreen(
    notificationViewModel: NotificationViewModel,
    authViewModel: AuthViewModel = hiltViewModel(),
    onRequestsClick: () -> Unit,
    onProgramClick: () -> Unit,
    onRequestResultClick: () -> Unit
) {
    val currentUser = authViewModel.currentUser.collectAsState().value
    val notifications = notificationViewModel.notifications.collectAsState().value

    LaunchedEffect(Unit) {
        notificationViewModel.loadNotifications()
    }

    val activity = LocalActivity.current as? ComponentActivity

    DisposableEffect(Unit) {
        onDispose {
            val isChangingConfigurations = activity?.isChangingConfigurations == true
            if (!isChangingConfigurations) {
                notificationViewModel.deleteAllNotifications()
            }
        }
    }

    NotificationContent(
        role = currentUser?.role,
        notifications = notifications,
        onRequestsClick = onRequestsClick,
        onProgramClick = onProgramClick,
        onRequestResultClick = onRequestResultClick
    )
}


@Composable
private fun NotificationContent(
    role: UserRole?,
    notifications: List<Notification>,
    onRequestsClick: () -> Unit,
    onProgramClick: () -> Unit,
    onRequestResultClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 20.dp)
    ) {
        when (role) {
            UserRole.COACH -> coachNotifications(
                notifications = notifications,
                onRequestsClick = onRequestsClick
            )
            UserRole.CLIENTE -> clienteNotifications(
                notifications = notifications,
                onProgramClick = onProgramClick,
                onRequestResultClick = onRequestResultClick
            )
            else -> item {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Esegui il login")
                }
            }
        }
    }
}


private fun LazyListScope.coachNotifications(
    notifications: List<Notification>,
    onRequestsClick: () -> Unit
) {
    val requests = notifications.filter { it.notificationType == NotificationType.CoachingRequest }

    if (requests.isEmpty()) {
        item {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text("Nessuna nuova richiesta")
            }
        }
    } else {
        items(requests) { _ ->
            ElevatedCardComp(onClick = onRequestsClick) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Nuova richiesta di coaching",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}


private fun LazyListScope.clienteNotifications(
    notifications: List<Notification>,
    onProgramClick: () -> Unit,
    onRequestResultClick: () -> Unit
) {
    val requestResult = notifications.firstOrNull { it.notificationType == NotificationType.CoachingRequestResult }
    val programNotifications = notifications.filter {
        it.notificationType == NotificationType.NewProgram || it.notificationType == NotificationType.ProgramUpdated
    }

    if (notifications.isEmpty()) {
        item {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text("Nessuna nuova notifica")
            }
        }
        return
    }

    requestResult?.let {
        item {
            ElevatedCardComp(
                onClick = onRequestResultClick
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        when (it.isAccepted) {
                            true -> "La tua richiesta di coaching è stata accettata!"
                            false -> "La tua richiesta di coaching è stata rifiutata"
                            null -> "Hai ricevuto una risposta alla tua richiesta di coaching"
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    items(programNotifications) { notification ->
        ElevatedCardComp(onClick = onProgramClick) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (notification.notificationType == NotificationType.NewProgram) "Hai ricevuto un nuovo programma"
                    else "Il tuo programma è stato modificato",
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewNotificationsScreen() {
    ProgettoWOCTheme() {
        NotificationContent(
            role = UserRole.CLIENTE,
            notifications = listOf(
                Notification(
                    userId = "",
                    type = "newProgram",
                    createdAt = "oggi",
                    isAccepted = null
                ),
                Notification(
                    userId = "",
                    type = "programUpdated",
                    createdAt = "ieri",
                    isAccepted = null
                ),
                Notification(
                    userId = "",
                    type = "coachingRequestResult",
                    createdAt = "2 giorni fa",
                    isAccepted = true
                )
            ),
            onRequestsClick = {},
            onProgramClick = {},
            onRequestResultClick = {}
        )
    }
}