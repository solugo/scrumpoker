package de.solugo.messages.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("revealRoom")
data class RevealRoomRequest(
    val roomId: String,
    val visible: Boolean = true,
) : Request()
