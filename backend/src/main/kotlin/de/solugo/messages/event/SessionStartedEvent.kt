package de.solugo.messages.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("sessionStarted")
data class SessionStartedEvent(
    val playerId: String,
) : Event()
