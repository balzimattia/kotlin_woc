package com.example.progettowoc.coaching.data

import com.example.progettowoc.supabase.Tables
import com.example.progettowoc.users.data.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

interface CoachingRequestRepositoryInterface {
    suspend fun addRequest(coachId: String)
    suspend fun updateRequest(isAccepted: Boolean, clienteId: String)
    suspend fun retrieveRequestsList(): List<User>
    suspend fun getClientePendingRequest(clienteId: String): RequestStatus?
    suspend fun deletePendingRequest()
}


interface CoachingRelationRepositoryInterface {
    suspend fun removeCoachingRelation(clienteId: String)
    suspend fun getClienteOwnCoach(clienteId: String): User?
}


class CoachingRepository @Inject constructor(
    private val supabase: SupabaseClient
) : CoachingRequestRepositoryInterface, CoachingRelationRepositoryInterface {

    private val cRequestTable = Tables.CoachingRequests
    private val cRelationTable = Tables.CoachingRelations


    override suspend fun addRequest(coachId: String) {
        val clienteId = supabase.auth.currentUserOrNull()?.id

        if (clienteId != null) {
            val request = CoachingRequest(
                coachId = coachId,
                clienteId = clienteId,
                status = RequestStatus.PENDING
            )

            supabase.from(cRequestTable.TABLE_NAME).insert(request)
        }
    }


    override suspend fun updateRequest(isAccepted: Boolean, clienteId: String) {
        val currentUserId = supabase.auth.currentUserOrNull()?.id
        if (currentUserId != null) {
            val newStatus: RequestStatus = if(isAccepted) RequestStatus.ACCEPTED else RequestStatus.REJECTED

            supabase.from(cRequestTable.TABLE_NAME).update(
                { set(cRequestTable.STATUS, newStatus.toStatusString) }
            ) {
                filter {
                    eq(cRequestTable.CLIENTE_ID, clienteId)
                    eq(cRequestTable.STATUS, RequestStatus.PENDING.toStatusString)
                }
            }

            if (isAccepted) {
                val coachingRelation =
                    CoachingRelation(coachId = currentUserId, clienteId = clienteId)
                supabase.from(cRelationTable.TABLE_NAME).insert(coachingRelation)
            }
        }
    }


    override suspend fun removeCoachingRelation(clienteId: String) {
        supabase.from("coachingRelations").delete {
            filter {
                eq("clienteId", clienteId)
            }
        }
    }


    override suspend fun retrieveRequestsList(): List<User> {
        val currentUserId = supabase.auth.currentUserOrNull()?.id ?: throw Exception("utente non loggato")
        val usersTable = Tables.Users

        return supabase.from(usersTable.TABLE_NAME)
            .select(Columns.raw("*, ${cRequestTable.TABLE_NAME}!inner!clienteId(*)")) {
                filter {
                    eq("${cRequestTable.TABLE_NAME}.${cRequestTable.COACH_ID}", currentUserId)
                    eq("${cRequestTable.TABLE_NAME}.${cRequestTable.STATUS}", RequestStatus.PENDING.toStatusString)
                }
            }.decodeList<User>()
    }


    override suspend fun getClienteOwnCoach(clienteId: String): User? {
        val users = Tables.Users
        return supabase.from(users.TABLE_NAME)
            .select(columns = Columns.raw("*, ${cRelationTable.TABLE_NAME}!inner!coachId(*)")) {
                filter {
                    eq("${cRelationTable.TABLE_NAME}.${cRelationTable.CLIENTE_ID}", clienteId)
                }
        }.decodeSingleOrNull<User>()
    }


    override suspend fun getClientePendingRequest(clienteId: String): RequestStatus? {
        return supabase.from(cRequestTable.TABLE_NAME).select {
            filter {
                eq(cRequestTable.CLIENTE_ID, clienteId)
                eq(cRequestTable.STATUS, RequestStatus.PENDING.toStatusString)
            }
        }.decodeSingleOrNull<CoachingRequest>()?.status
    }


    override suspend fun deletePendingRequest() {
        val clienteId = supabase.auth.currentUserOrNull()?.id ?: return
        supabase.from(cRequestTable.TABLE_NAME).delete {
            filter {
                eq(cRequestTable.CLIENTE_ID, clienteId)
                eq(cRequestTable.STATUS, RequestStatus.PENDING.toStatusString)
            }
        }
    }
}