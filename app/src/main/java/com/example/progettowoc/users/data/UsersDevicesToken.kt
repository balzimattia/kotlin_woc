package com.example.progettowoc.users.data

import kotlinx.serialization.Serializable

@Serializable
data class UsersDevicesToken(
    val userId: String,
    val deviceId: String,
    val fcmToken: String?
)