package de.solugo.scrumpoker.model

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.openapitools.jackson.nullable.JsonNullable

@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME, property = "type")
sealed interface Action {

    data class UpdatePlayerInfo(
        val participate: JsonNullable<Boolean> = JsonNullable.undefined(),
        val name: JsonNullable<String?> = JsonNullable.undefined(),
        val selection: JsonNullable<String?> = JsonNullable.undefined(),
    ) : Action

}