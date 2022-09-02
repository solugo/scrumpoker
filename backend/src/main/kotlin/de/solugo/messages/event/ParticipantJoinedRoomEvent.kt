package de.solugo.messages.event

import de.solugo.model.ParticipantRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("participantJoinedRoom")
data class ParticipantJoinedRoomEvent(
    val roomId: String,
    val participantId: String,
    val role: ParticipantRole,
    val name: String,
) : Event()