package com.example.progettowoc.notifications.data

import com.example.progettowoc.supabase.Tables
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject


interface NotificationsRepositoryInterface {
    suspend fun getNotification(): List<Notification>
    suspend fun deleteAllNotification()
}


class NotificationRepository @Inject constructor(
    private val supabase: SupabaseClient
): NotificationsRepositoryInterface {

    private val auth = supabase.auth
    private val notTable = Tables.Notifications

    override suspend fun getNotification(): List<Notification> {
        val userId = auth.currentUserOrNull()?.id ?: return emptyList()
        return supabase.from(notTable.TABLE_NAME).select {
            filter {
                eq(notTable.USER_ID, userId)
            }
            order(notTable.CREATED_AT, Order.DESCENDING)
        }.decodeList<Notification>()
    }


    override suspend fun deleteAllNotification() {
        val userId = auth.currentUserOrNull()?.id ?: return
        supabase.from(notTable.TABLE_NAME).delete {
            filter {
                eq(notTable.USER_ID, userId)
            }
        }
    }
}