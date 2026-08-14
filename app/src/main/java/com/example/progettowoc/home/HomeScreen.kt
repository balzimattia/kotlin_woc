package com.example.progettowoc.home


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progettowoc.ui.theme.ProgettoWOCTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.progettowoc.auth.viewmodels.AuthViewModel
import com.example.progettowoc.ui.theme.LightGreen
import com.example.progettowoc.users.data.User
import com.example.progettowoc.users.data.UserRole
import io.github.jan.supabase.auth.status.SessionStatus

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onLoginClick: () -> Unit,
    onRegistratiClick: () -> Unit,
    onNextWorkOutClick: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val sessionStatus by authViewModel.sessionStatus.collectAsState()

    val isTransitioning = sessionStatus is SessionStatus.Initializing ||
            (sessionStatus is SessionStatus.Authenticated && currentUser == null)

    if (isTransitioning) {
        CircularProgressIndicator()
    } else {
        HomeContent(
            currentUser = currentUser,
            onLoginClick = onLoginClick,
            onRegistratiClick = onRegistratiClick,
            onNextWorkOutClick = onNextWorkOutClick
        )
    }
}


@Composable
private fun HomeContent(
    currentUser: User?,
    onLoginClick: () -> Unit,
    onRegistratiClick: () -> Unit,
    onNextWorkOutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 50.dp, vertical = 80.dp)
            .semantics { testTag = "home_screen" },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "BENVENUTO\n${currentUser?.name ?: "IN\nWORKOUT COACHING"}",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 42.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayMedium
        )

        if (currentUser == null) {
            NotLogged(onLoginClick, onRegistratiClick)
        } else {
            if(currentUser.role == UserRole.CLIENTE)
                ClienteLogged(
                    onNextWorkOutClick = onNextWorkOutClick
                )
        }
    }
}


@Composable
private fun NotLogged(
    onLoginClick: () -> Unit,
    onRegistratiClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        ElevatedButton(
            onClick = onLoginClick,
            shape = RoundedCornerShape(30.dp),
            elevation = ButtonDefaults.buttonElevation(10.dp),
            modifier = Modifier
                .height(60.dp)
                .width(250.dp),
            colors = ButtonColors(
                containerColor = LightGreen,
                contentColor = Color.Black,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.White
            )
        ) {
            Text("ACCEDI", fontSize = 32.sp)
        }

        TextButton(
            modifier = Modifier.semantics { testTag = "registrati" },
            onClick = { onRegistratiClick() }
        ) {
            Text(
                text = "O registrati gratuitamente",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}


@Composable
private fun ClienteLogged(
    onNextWorkOutClick: () -> Unit
) {
    ElevatedButton(
        onClick = onNextWorkOutClick,
        shape = RoundedCornerShape(10.dp),
        elevation = ButtonDefaults.buttonElevation(10.dp),
        modifier = Modifier
            .height(70.dp)
            .width(250.dp),
        colors = ButtonColors(
            containerColor = LightGreen,
            contentColor = Color.Black,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "Accedi al prossimo allenamento",
                    color = Color.Black,
                    fontSize = 18.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "vai all'esercizio",
                tint = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    ProgettoWOCTheme {
        HomeContent(null, {}, {}, {})
        //HomeContent(User("", "", "prova prova", UserRole.CLIENTE), {}, {}, {})
    }
}