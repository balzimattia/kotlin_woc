package com.example.progettowoc.programs.data


import com.example.progettowoc.supabase.Tables
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject


interface ProgramsRepositoryInterface{
    suspend fun getProgram(clienteId: String, programNumber: Int): ProgramSheet
    suspend fun getLatestProgramNumber(clienteId: String): Int
    suspend fun getProgramsList(clienteId: String): List<ProgramSheet>
}


interface ClienteProgramsRepositoryInterface: ProgramsRepositoryInterface{
    suspend fun updateProgramAsCliente(updatedProgramSheet: ProgramSheet)
}


interface CoachProgramsRepositoryInterface: ProgramsRepositoryInterface{
    suspend fun addProgram(clienteId: String, programSheet: ProgramSheet)
    suspend fun updateProgram(clienteId: String, updatedProgramSheet: ProgramSheet)
}


class ProgramsRepository @Inject constructor(
    private val supabase: SupabaseClient
) : ClienteProgramsRepositoryInterface, CoachProgramsRepositoryInterface {

    private val pTable = Tables.Programs


    private suspend fun getRow(clienteId: String): Program {
        return supabase.from(pTable.TABLE_NAME)
            .select { filter { eq(pTable.CLIENTE_ID, clienteId) } }
            .decodeSingle<Program>()
    }


    override suspend fun addProgram(clienteId: String, programSheet: ProgramSheet) {
        val row = getRow(clienteId)
        val newNum = (row.latestProgramNum ?: 0) + 1
        val updatedPrograms = row.program + programSheet.copy(number = newNum)

        supabase.from(pTable.TABLE_NAME).update({
            set(pTable.PROGRAM, updatedPrograms)
            set(pTable.LATEST_PROGRAM_NUM, newNum)
        }) { filter { eq(pTable.CLIENTE_ID, clienteId) } }
    }


    override suspend fun updateProgram(clienteId: String, updatedProgramSheet: ProgramSheet) {
        val row = getRow(clienteId)
        val updatedPrograms = row.program.map {
            if (it.number == updatedProgramSheet.number) updatedProgramSheet else it
        }

        supabase.from(pTable.TABLE_NAME).update({
            set(pTable.PROGRAM, updatedPrograms)
        }) { filter { eq(pTable.CLIENTE_ID, clienteId) } }
    }


    override suspend fun updateProgramAsCliente(updatedProgramSheet: ProgramSheet) {
        val clienteId = supabase.auth.currentUserOrNull()?.id
            ?: throw Exception("Utente non loggato")
        updateProgram(clienteId, updatedProgramSheet)
    }


    override suspend fun getProgram(clienteId: String, programNumber: Int): ProgramSheet {
        return getRow(clienteId).program.first { it.number == programNumber }
    }


    override suspend fun getLatestProgramNumber(clienteId: String): Int {
        return getRow(clienteId).latestProgramNum ?: 0
    }


    override suspend fun getProgramsList(clienteId: String): List<ProgramSheet> {
        return getRow(clienteId).program
    }
}