package com.example.progettowoc.coaching.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RequestStatus(val toStatusString: String) {
    @SerialName("Pending")
    PENDING("Pending"),
    @SerialName("Accepted")
    ACCEPTED("Accepted"),
    @SerialName("Rejected")
    REJECTED("Rejected")
}


@Serializable
data class CoachingRequest(
    val coachId: String,
    val clienteId: String,
    val status: RequestStatus
)