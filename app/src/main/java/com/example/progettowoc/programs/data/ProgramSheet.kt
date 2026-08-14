package com.example.progettowoc.programs.data

import kotlinx.serialization.Serializable


@Serializable
data class ProgramSheet(
    val number: Int,
    val weeks: List<Week>
)


@Serializable
data class Week(
    val number: Int,
    val days: List<Day>
)


@Serializable
data class Day(
    val number: Int,
    val isCompleted: Boolean,
    val exercises: List<Exercise>
)


@Serializable
data class Exercise(
    val name: String,
    val sets: Int,
    val reps: Int,
    val rest: Int, //in secondi
    val weight: Float,
    val coachComment: String,
    val clienteComment: String
)