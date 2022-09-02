package de.solugo.messages.request

import de.solugo.model.ParticipantRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("joinRoom")
data class JoinRoomRequest(
    val roomId: String? = null,
    val name: String,
    val role: ParticipantRole = ParticipantRole.PLAYER,
) : Request()
