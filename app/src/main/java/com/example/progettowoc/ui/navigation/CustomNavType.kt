package com.example.progettowoc.ui.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.example.progettowoc.programs.data.ProgramSheet
import com.example.progettowoc.users.data.User
import kotlinx.serialization.json.Json

object CustomNavType {

    val UserNavType = object : NavType<User>(isNullableAllowed = false) {
        override fun get(bundle: Bundle, key: String): User? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): User {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun put(bundle: Bundle, key: String, value: User) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun serializeAsValue(value: User): String {
            return Uri.encode(Json.encodeToString(value))
        }
    }


    val ProgramSheetNavType = object : NavType<ProgramSheet?>(isNullableAllowed = true) {
        override fun get(bundle: Bundle, key: String): ProgramSheet? {
            return bundle.getString(key)?.let { Json.decodeFromString(it) }
        }

        override fun parseValue(value: String): ProgramSheet? {
            return if (value == "null") null else Json.decodeFromString(Uri.decode(value))
        }

        override fun put(bundle: Bundle, key: String, value: ProgramSheet?) {
            bundle.putString(key, value?.let { Json.encodeToString(it) })
        }

        override fun serializeAsValue(value: ProgramSheet?): String {
            return value?.let { Uri.encode(Json.encodeToString(it)) } ?: "null"
        }
    }
}