package de.solugo.scrumpoker.model

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME, property = "type")
sealed interface Event {

    data class Error(
        val message: String,
    ) : Event

    data class PlayerJoined(
        val id: String,
    ) : Event

    data class PlayerLeft(
        val id: String,
    ) : Event

    data class PlayerInfoUpdated(
        val id: String,
        val name: String? = null,
        val participate: Boolean? = null,
        val selection: String? = null,
    ) : Event

    data class SelectionsChanged(
        val selections: Map<String, String?>,
    ) : Event

}