package com.example.progettowoc.notifications


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.progettowoc.MainActivity
import com.example.progettowoc.R
import com.example.progettowoc.device.DeviceHelper
import com.example.progettowoc.notifications.data.NotificationPreferencesDataStore
import com.example.progettowoc.supabase.SupabaseClientImpl
import com.example.progettowoc.supabase.Tables
import com.example.progettowoc.users.data.UsersDevicesToken
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random


@AndroidEntryPoint
class MessagingService: FirebaseMessagingService() {

    @Inject
    lateinit var notificationPreferencesDataStore: NotificationPreferencesDataStore

    private val supabase = SupabaseClientImpl.getSupabaseClient()


    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val userId = supabase.auth.currentUserOrNull()?.id ?: return

        val deviceId = DeviceHelper.getDeviceId(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            supabase.from(Tables.UsersDevicesToken.TABLE_NAME)
                .upsert(
                    UsersDevicesToken(
                        userId = userId,
                        deviceId = deviceId,
                        fcmToken = token
                    )
                )
        }
    }


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        if (data.isEmpty()) return

        val incomingNotification = NotificationType.fromMap(data)
        val title = data["title"] ?: return
        val body = data["body"] ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val enabled = notificationPreferencesDataStore.isEnabled(incomingNotification)
            if (enabled) {
                showNotification(title, body, incomingNotification)
            }
            NotificationViewModel.notificationReceived.emit(Unit)
        }
    }


    private fun showNotification(
        title: String,
        body: String,
        notification: NotificationType
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            when (notification) {
                is NotificationType.CoachingRequest -> putExtra("TYPE", "coachingRequest")
                is NotificationType.CoachingRequestResult -> putExtra("TYPE", "coachingRequestResult")
                is NotificationType.NewProgram -> putExtra("TYPE", "newProgram")
                is NotificationType.ProgramUpdated -> putExtra("TYPE", "programUpdated")
                NotificationType.Generic -> putExtra("TYPE", "generic")
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            Random.nextInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
        )

        val channelId = when (notification) {
            is NotificationType.CoachingRequest -> NotificationChannels.COACHING_REQUEST_ID
            is NotificationType.CoachingRequestResult -> NotificationChannels.REQUEST_RESULT_ID
            is NotificationType.NewProgram -> NotificationChannels.PROGRAM_ID
            is NotificationType.ProgramUpdated -> NotificationChannels.PROGRAM_ID
            NotificationType.Generic -> NotificationChannels.COACHING_REQUEST_ID
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .notify(Random.nextInt(), notificationBuilder)
    }
}