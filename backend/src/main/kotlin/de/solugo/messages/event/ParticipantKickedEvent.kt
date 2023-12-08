package de.solugo.messages.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("participantKicked")
data class ParticipantKickedEvent(
    val roomId: String,
    val participantId: String,
    val initiatorParticipantId: String,
) : Event()