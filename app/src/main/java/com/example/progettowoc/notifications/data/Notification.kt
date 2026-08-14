package com.example.progettowoc.notifications.data

import com.example.progettowoc.notifications.NotificationType
import kotlinx.serialization.Serializable


@Serializable
data class Notification(
    val userId: String,
    val type: String,
    val isAccepted: Boolean?,
    val createdAt: String
) {
    val notificationType: NotificationType
        get() = when (type) {
            "coachingRequest" -> NotificationType.CoachingRequest
            "coachingRequestResult" -> NotificationType.CoachingRequestResult
            "newProgram" -> NotificationType.NewProgram
            "programUpdated" -> NotificationType.ProgramUpdated
            else -> NotificationType.Generic
        }
}