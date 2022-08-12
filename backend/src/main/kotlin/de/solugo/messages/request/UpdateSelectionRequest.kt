package de.solugo.messages.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("updateSelection")
data class UpdateSelectionRequest(
    val roomId: String,
    val selection: String?,
) : Request()