package de.solugo.messages.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("participantLeftRoom")
data class ParticipantLeftRoomEvent(
    val roomId: String,
    val participantId: String,
) : Event()