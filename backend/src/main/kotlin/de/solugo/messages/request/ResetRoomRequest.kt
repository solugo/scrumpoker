package de.solugo.messages.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("resetRoom")
data class ResetRoomRequest(
    val roomId: String,
) : Request()
