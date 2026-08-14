package com.example.progettowoc.coaching.data

import kotlinx.serialization.Serializable

@Serializable
data class CoachingRelation(
    val coachId: String,
    val clienteId: String
)