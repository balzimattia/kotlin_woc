package com.example.progettowoc.notifications.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.progettowoc.notifications.NotificationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


// tipi di notifica da attivare o disattivare nelle impostazioni app
class NotificationPreferencesDataStore(private val context: Context) {

    private val Context.dataStore by preferencesDataStore(name = "notification_preferences")

    companion object {
        val COACHING_REQUEST_ENABLED = booleanPreferencesKey("coaching_request_enabled")
        val REQUEST_RESULT_ENABLED = booleanPreferencesKey("request_result_enabled")
        val PROGRAM_ENABLED = booleanPreferencesKey("program_enabled")
    }

    suspend fun isEnabled(type: NotificationType): Boolean {
        val prefs = context.dataStore.data.first()
        return when (type) {
            is NotificationType.CoachingRequest -> prefs[COACHING_REQUEST_ENABLED] ?: true
            is NotificationType.CoachingRequestResult -> prefs[REQUEST_RESULT_ENABLED] ?: true
            is NotificationType.NewProgram -> prefs[PROGRAM_ENABLED] ?: true
            is NotificationType.ProgramUpdated -> prefs[PROGRAM_ENABLED] ?: true
            NotificationType.Generic -> true
        }
    }

    suspend fun setEnabled(type: NotificationType, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            when (type) {
                is NotificationType.CoachingRequest -> prefs[COACHING_REQUEST_ENABLED] = enabled
                is NotificationType.CoachingRequestResult -> prefs[REQUEST_RESULT_ENABLED] = enabled
                is NotificationType.NewProgram -> prefs[PROGRAM_ENABLED] = enabled
                is NotificationType.ProgramUpdated -> prefs[PROGRAM_ENABLED] = enabled
                NotificationType.Generic -> {}
            }
        }
    }

    fun getAll(): Flow<Map<NotificationType, Boolean>> {
        return context.dataStore.data.map { prefs ->
            mapOf(
                NotificationType.CoachingRequest to (prefs[COACHING_REQUEST_ENABLED] ?: true),
                NotificationType.CoachingRequestResult to (prefs[REQUEST_RESULT_ENABLED] ?: true),
                NotificationType.NewProgram to (prefs[PROGRAM_ENABLED] ?: true),
            )
        }
    }
}