package de.solugo.messages.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("playerInfoChanged")
data class PlayerInfoChangedEvent(
    val roomId: String,
    val playerId: String,
    val name: String? = null,
) : Event()
