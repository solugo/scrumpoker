package de.solugo.messages.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("updatePlayerInfo")
data class UpdatePlayerInfoRequest(
    val roomId: String,
    val name: String? = null,
) : Request()