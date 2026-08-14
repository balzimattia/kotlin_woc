package com.example.progettowoc.users.data

import android.util.Log
import com.example.progettowoc.supabase.Tables
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject


// deve poter leggere gli utenti dal db, ma non aggiornarli


interface ClienteUserRepositoryInterface {
    suspend fun searchCoachesList(search: String): List<User>
}


interface CoachUserRepositoryInterface {
    suspend fun getClientiList(coachId: String): List<User>
}


class UserRepository @Inject constructor(
    private val supabase: SupabaseClient
): ClienteUserRepositoryInterface, CoachUserRepositoryInterface {
    private val users = Tables.Users


    override suspend fun searchCoachesList(search: String): List<User> {
        return supabase.from(users.TABLE_NAME).select {
            filter {
                eq(users.ROLE, UserRole.COACH.toRoleString)
                ilike(users.NAME, "%${search}%")
            }
        }.decodeList<User>()
    }


    override suspend fun getClientiList(coachId: String): List<User> {
        val cr = Tables.CoachingRelations

        return supabase.from(users.TABLE_NAME)
            .select(Columns.raw("""*, ${cr.TABLE_NAME}!${cr.CLIENTE_ID}!inner()""".trimIndent())) {
                filter {
                    eq(users.ROLE, UserRole.CLIENTE.toRoleString)
                    eq("${cr.TABLE_NAME}.${cr.COACH_ID}", coachId)
                }
            }
            .decodeList<User>()
    }
}