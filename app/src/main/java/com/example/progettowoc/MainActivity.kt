package com.example.progettowoc

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.progettowoc.ui.screen.AppScreen
import com.example.progettowoc.ui.theme.ProgettoWOCTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.progettowoc.auth.viewmodels.AuthViewModel
import com.example.progettowoc.notifications.NotificationChannels
import com.example.progettowoc.users.viewmodels.SettingsViewModel
import dagger.Provides
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.auth.status.SessionStatus
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    @Inject
    lateinit var networkObserver:NetworkObserver


    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        //aspetta di vedere se ce il collegamento alla rete, il caricamente dell'utente o la verifica che sia loggato
        splashScreen.setKeepOnScreenCondition {
            val status = authViewModel.sessionStatus.value
            val user = authViewModel.currentUser.value
            val isConnected = networkObserver.isConnected.value

            if (!isConnected) return@setKeepOnScreenCondition false

            return@setKeepOnScreenCondition when (status) {
                is SessionStatus.Initializing -> true
                is SessionStatus.Authenticated -> user == null
                is SessionStatus.NotAuthenticated -> false
                else -> false
            }
        }

        NotificationChannels.createChannels(this)

        enableEdgeToEdge()

        setContent {
            MyApp(networkObserver = networkObserver)
        }
    }
}



@Composable
private fun NotificationPermission() {
    val notificationPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )

    LaunchedEffect(Unit) {
        notificationPermissionResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}



@Composable
private fun MyApp(networkObserver: NetworkObserver) {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

    ProgettoWOCTheme(darkTheme = isDarkTheme) {

        NotificationPermission()

        AppScreen(networkObserver = networkObserver)
    }
}

@Preview(showBackground = true)
@Composable
private fun MyAppPreview() {
    ProgettoWOCTheme {}
}