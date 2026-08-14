package com.example.progettowoc.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object NotificationChannels {
    const val COACHING_REQUEST_ID = "coaching_request"
    const val COACHING_REQUEST_NAME = "Richieste di coaching"

    const val REQUEST_RESULT_ID = "request_result"
    const val REQUEST_RESULT_NAME = "Risposta richieste di coaching"

    const val PROGRAM_ID = "program"
    const val PROGRAM_NAME = "Programmi di allenamento"

    @SuppressLint("ServiceCast")
    fun createChannels(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        listOf(
            NotificationChannel(
                COACHING_REQUEST_ID,
                COACHING_REQUEST_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ),
            NotificationChannel(
                REQUEST_RESULT_ID,
                REQUEST_RESULT_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ),
            NotificationChannel(
                PROGRAM_ID,
                PROGRAM_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
        ).forEach { manager.createNotificationChannel(it) }
    }
}