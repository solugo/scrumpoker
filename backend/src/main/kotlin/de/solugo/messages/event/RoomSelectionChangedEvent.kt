package de.solugo.messages.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("roomSelectionChanged")
data class RoomSelectionChangedEvent(
    val roomId: String,
    val visible: Boolean,
    val selections: Map<String, String?>,
) : Event()