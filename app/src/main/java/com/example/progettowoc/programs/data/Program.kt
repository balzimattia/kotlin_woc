package com.example.progettowoc.programs.data

import kotlinx.serialization.Serializable


@Serializable
data class Program(
    val coachId: String,
    val clienteId: String,
    val program: List<ProgramSheet>,
    val latestProgramNum: Int? = null
)
