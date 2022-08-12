package de.solugo.messages.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("leaveRoom")
data class LeaveRoomRequest(
    val roomId: String,
) : Request()
