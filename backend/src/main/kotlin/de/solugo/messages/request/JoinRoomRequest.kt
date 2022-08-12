package de.solugo.messages.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("joinRoom")
data class JoinRoomRequest(
    val roomId: String? = null,
) : Request()
