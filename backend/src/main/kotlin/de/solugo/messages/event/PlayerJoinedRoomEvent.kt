package de.solugo.messages.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("playerJoinedRoom")
data class PlayerJoinedRoomEvent(
    val roomId: String,
    val playerId: String,
) : Event()