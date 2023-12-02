package de.solugo.messages.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("kickParticipant")
data class KickParticipantRequest(
    val roomId: String,
    val participantId: String,
) : Request()