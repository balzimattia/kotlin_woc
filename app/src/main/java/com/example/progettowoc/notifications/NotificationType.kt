package com.example.progettowoc.notifications


//sealed class per distinguere i vari tipi di notifica
sealed class NotificationType {
    object CoachingRequest : NotificationType()
    object CoachingRequestResult : NotificationType()
    object NewProgram : NotificationType()
    object ProgramUpdated : NotificationType()
    object Generic : NotificationType()

    companion object {
        fun fromMap(data: Map<String, String>): NotificationType {
            return when (data["type"]) {
                "coachingRequest" -> CoachingRequest
                "coachingRequestResult" -> CoachingRequestResult
                "newProgram" -> NewProgram
                "programUpdated" -> ProgramUpdated
                else -> Generic
            }
        }
    }
}