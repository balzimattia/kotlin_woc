package com.example.progettowoc.device


import android.content.Context
import java.util.UUID
import androidx.core.content.edit


// serve per identificare il dispositivo nella tabella dei token
object DeviceHelper {
    private const val PREFS_NAME = "device_prefs"
    private const val DEVICE_ID_KEY = "deviceId"


    fun getDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        var deviceId = prefs.getString(DEVICE_ID_KEY, null)

        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString()

            prefs.edit { putString(DEVICE_ID_KEY, deviceId) }
        }

        return deviceId
    }
}