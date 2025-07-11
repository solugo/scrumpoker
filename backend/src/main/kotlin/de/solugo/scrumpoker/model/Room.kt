package de.solugo.scrumpoker.model

data class Room(
    val players: MutableMap<String, Player> = hashMapOf(),
    var options: Map<String, Int?> = emptyMap(),
    val visible: Boolean = false,
    var name: String? = null,
)