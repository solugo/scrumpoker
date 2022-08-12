package de.solugo.messages.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("roomInfoChanged")
data class RoomInfoChangedEvent(
    val roomId: String,
    val name: String? = null,
    val options: Map<String, Double?>,
) : Event()
