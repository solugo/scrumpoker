package de.solugo.scrumpoker.model

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.openapitools.jackson.nullable.JsonNullable

@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME, property = "type")
sealed interface Event {

    data class Error(
        val message: String,
    ) : Event

    data class RoomUpdated(
        val name: JsonNullable<String?> = JsonNullable.undefined(),
        val visible: JsonNullable<Boolean> = JsonNullable.undefined(),
        val options: JsonNullable<Map<String, Int?>> = JsonNullable.undefined(),
    ) : Event

    data class PlayerUpdated(
        val id: String,
        val state: JsonNullable<Player.State> = JsonNullable.undefined(),
        val name: JsonNullable<String?> = JsonNullable.undefined(),
        val participate: JsonNullable<Boolean> = JsonNullable.undefined(),
        val selected: JsonNullable<Boolean> = JsonNullable.undefined(),
    ) : Event

    data class ResultUpdated(
        val selections: Map<String, String?>? = null,
        val statistics: Statistics? = null,
        val discussion: Discussion? = null,
    ) : Event {
        data class Statistics(
            val min: Int? = null,
            val max: Int? = null,
            val average: Double? = null,
            val median: Int? = null,
        )

        data class Discussion(
            val distance: Int? = null,
            val playerLow: String? = null,
            val playerHigh: String? = null,
        )
    }

}