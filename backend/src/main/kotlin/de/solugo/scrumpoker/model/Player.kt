package de.solugo.scrumpoker.model

data class Player(
    var name: String? = null,
    var state: State = State.DISCONNECTED,
    var participate: Boolean = true,
    var selection: String? = null,
) {
    enum class State {
        CONNECTED, DISCONNECTED
    }
}