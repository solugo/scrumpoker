package de.solugo.messages.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("updateRoomInfo")
data class UpdateRoomInfoRequest(
    val roomId: String,
    val name: String? = null,
    val options: Map<String, Double?>? = null,
) : Request()