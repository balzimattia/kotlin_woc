package com.example.progettowoc.users.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
enum class UserRole(val toRoleString: String) {
    @SerialName("Coach")
    COACH("Coach"),
    @SerialName("Cliente")
    CLIENTE("Cliente")
}


@Serializable
data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: UserRole, // "coach" o "cliente"
)