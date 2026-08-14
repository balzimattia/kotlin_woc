package com.example.progettowoc.auth.data

import android.content.Context
import android.util.Log
import com.example.progettowoc.device.DeviceHelper
import com.example.progettowoc.supabase.Tables
import com.example.progettowoc.users.data.User
import com.example.progettowoc.users.data.UserRole
import com.example.progettowoc.users.data.UsersDevicesToken
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


interface AuthRepositoryInterface {
    val currentUser: StateFlow<User?>
    val sessionStatus: StateFlow<SessionStatus>
    suspend fun registerUser(email: String, password: String, name: String, role: UserRole)
    suspend fun login(email: String, password: String)
    suspend fun logout()
    suspend fun updatePassword(newPassword: String)
}

class AuthRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val context: Context
) : AuthRepositoryInterface {
    private val auth = supabase.auth

    override val sessionStatus: StateFlow<SessionStatus> = auth.sessionStatus

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private var registerFlag = false


    // inizializza current user se la sessione esiste ad apertura app
    // update in caso di authenticated o no, non ce bisogno di gestire dentro login e logout
    init {
        MainScope().launch {
            auth.sessionStatus.collect { status ->
                when(status) {
                    is SessionStatus.Authenticated -> {
                        if (!registerFlag) {
                            try {
                                _currentUser.value = getUserDb()
                            } catch (e: Exception) {
                                _currentUser.value = null
                                logout()
                            }
                        }
                    }

                    is SessionStatus.NotAuthenticated -> {
                        _currentUser.value = null
                    }

                    else -> {
                        _currentUser.value = null
                    }
                }
            }
        }
    }


    // registra un nuovo utente
    override suspend fun registerUser(email: String, password: String, name: String, role: UserRole) {
        registerFlag = true

        try {
            val authUser = auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            val userId = authUser?.id ?: throw IllegalStateException("Errore registrazione")
            val token = retrieveFCMToken()

            val user = User(
                id = userId,
                email = email,
                name = name,
                role = role
            )

            val deviceId = DeviceHelper.getDeviceId(context)
            val udt = UsersDevicesToken(
                userId = userId,
                deviceId = deviceId,
                fcmToken = token
            )

            supabase.from(Tables.Users.TABLE_NAME).insert(user)
            supabase.from(Tables.UsersDevicesToken.TABLE_NAME).upsert(udt)

            _currentUser.value = user
            Log.e("user da register", _currentUser.toString())
        } finally {
            registerFlag = false
        }
    }


    // login, con insreimento del fcm token nella tabella
    override suspend fun login(email: String, password: String) {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }

        val id = getCurrentUserId()
        val token = retrieveFCMToken()
        val deviceId = DeviceHelper.getDeviceId(context)
        addFCMToken(id, deviceId, token)
    }


    //logout dove toglie anche il fcmtoken dalla tabella
    override suspend fun logout() {
        val token = retrieveFCMToken()
        val deviceId = DeviceHelper.getDeviceId(context)

        try {
            removeFCMToken(deviceId, token)
        }
        catch (e: Exception) {
            Log.e("remove token:", e.stackTraceToString())
        }

        auth.signOut()
    }


    override suspend fun updatePassword(newPassword: String) {
        auth.currentUserOrNull() ?: throw IllegalStateException("Utente non autenticato")
        auth.updateUser {
            password = newPassword
        }
    }


    private suspend fun getUserDb(): User {
        val id = getCurrentUserId()
        val users = Tables.Users
        return supabase.from(users.TABLE_NAME).select {
            filter {
                eq(users.ID, id)
            }
        }.decodeList<User>().firstOrNull() ?: throw IllegalStateException("UserRoute non trovato")
    }


    private fun getCurrentUserId(): String {
        return auth.currentUserOrNull()?.id ?: throw IllegalStateException("Utente non loggato")
    }


    //token fcm per l'invio di notifiche
    private suspend fun retrieveFCMToken(): String? {
        return try{
            Firebase.messaging.token.await()
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun addFCMToken(userId: String, deviceId: String, token: String?) {
        if(!token.isNullOrBlank()) {
            val udt = UsersDevicesToken(userId = userId, deviceId = deviceId, fcmToken = token)
            supabase.from(Tables.UsersDevicesToken.TABLE_NAME).upsert(udt)
        }
    }


    private suspend fun removeFCMToken(deviceId: String, token: String?) {
        if (!token.isNullOrBlank()) {
            val udtTable = Tables.UsersDevicesToken
            supabase.from(udtTable.TABLE_NAME).delete {
                filter {
                    eq(udtTable.DEVICE_ID, deviceId)
                }
            }
        }
    }
}