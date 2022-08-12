package de.solugo.messages.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("playerLeftRoom")
data class PlayerLeftRoomEvent(
    val roomId: String,
    val playerId: String,
) : Event()