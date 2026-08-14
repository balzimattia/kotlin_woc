package com.example.progettowoc.supabase


object Tables {

    object Users {
        const val TABLE_NAME = "users"

        const val ID = "id"
        const val EMAIL = "email"
        const val NAME = "name"
        const val ROLE = "role"
    }

    object UsersDevicesToken {
        const val TABLE_NAME = "usersDevicesToken"

        const val DEVICE_ID = "deviceId"
        const val USER_ID = "userId"
        const val FCM_TOKEN = "fcmToken"
    }

    object CoachingRequests {
        const val TABLE_NAME = "coachingRequests"

        const val ID = "id"
        const val COACH_ID = "coachId"
        const val CLIENTE_ID = "clienteId"
        const val STATUS = "status"
    }

    object CoachingRelations {
        const val TABLE_NAME = "coachingRelations"

        const val ID = "id"
        const val COACH_ID = "coachId"
        const val CLIENTE_ID = "clienteId"
    }

    object Programs {
        const val TABLE_NAME = "programs"

        const val ID = "id"
        const val COACH_ID = "coachId"
        const val CLIENTE_ID = "clienteId"
        const val PROGRAM = "program"
        const val LATEST_PROGRAM_NUM = "latestProgramNum"
    }

    object Notifications {
        const val TABLE_NAME = "notifications"

        const val ID = "id"
        const val USER_ID = "userId"
        const val TYPE = "type"
        const val CREATED_AT = "createdAt"
        const val IS_ACCEPTED = "isAccepted"
    }
}
