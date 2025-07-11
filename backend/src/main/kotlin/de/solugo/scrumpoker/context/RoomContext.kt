package de.solugo.scrumpoker.context

import de.solugo.scrumpoker.model.Action
import de.solugo.scrumpoker.model.Event
import de.solugo.scrumpoker.model.Player
import de.solugo.scrumpoker.model.Room
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.openapitools.jackson.nullable.JsonNullable
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty

@Component
class RoomContext {
    private val mutex = Mutex()
    private val sessions = hashMapOf<String, RoomSession>()

    suspend fun session(roomId: String) = mutex.withLock {
        sessions.computeIfAbsent(roomId) { RoomSession() }
    }

    class RoomSession(
        val room: Room = Room(),
    ) {
        private val playerSend = hashMapOf<String, suspend (Event) -> Unit>()
        private val mutex = Mutex()

        suspend fun join(playerId: String, send: suspend (Event) -> Unit) = mutex.withLock {
            playerSend[playerId] = send
            room.players.computeIfAbsent(playerId) { Player() }.apply {
                state = Player.State.CONNECTED
            }
        }

        suspend fun leave(playerId: String) = mutex.withLock {
            room.players.remove(playerId)
            playerSend.remove(playerId)
        }

        suspend fun process(playerId: String, action: Action) = mutex.withLock {
            val modified = hashSetOf<KProperty<*>>()
            when (action) {
                is Action.UpdatePlayerInfo -> {
                    room.players[playerId]?.also { player ->
                        player::name.set(action.name, modified)
                        player::participate.set(action.participate, modified)
                        player::selection.set(action.selection, modified)
                    }
                }
            }

            generateEvents(modified).forEach { event ->
                playerSend.values.forEach {
                    runCatching {
                        it(event)
                    }.onFailure {
                        logger.error("Failed to process event $event", it)
                    }
                }
            }
        }

        private fun generateEvents(modified: Set<KProperty<*>>? = null) = buildList {
            val force = modified == null
            var recalc = force

            room.players.forEach { (id, player) ->
                modified.changes(player::name, player::participate, player::selection) {
                    add(
                        Event.PlayerUpdated(
                            id = id,
                            name = jsonNullable(player::name),
                            participate = jsonNullable(player::participate),
                            selected = jsonNullable(player::selection).letPresent { it != null },
                        )
                    )
                }
                modified.changes(player::participate, player::selection) {
                    recalc = true
                }
            }

            modified.changes(room::name) {
                add(
                    Event.RoomUpdated(
                        name = jsonNullable(room::name),
                        visible = jsonNullable(room::visible),
                    )
                )
            }

            modified.changes(force, room::visible) {
                recalc = true
            }

            if (recalc) {
                add(
                    when {
                        room.visible -> {
                            val playerSelections = buildMap {
                                room.players.forEach { (id, player) ->
                                    player.selection?.also { put(id, it) }
                                }
                            }
                            val playerValues = buildMap {
                                room.players.forEach { (id, player) ->
                                    room.options[player.selection]?.also { put(id, it) }
                                }
                            }

                            val values = playerValues.values.sorted()

                            val valuePlayers = playerValues.entries.groupBy(
                                keySelector = { it.value },
                                valueTransform = { it.key },
                            )

                            val min = values.firstOrNull()
                            val max = values.lastOrNull()
                            val median = values.getOrNull(values.size / 2)
                            val average = values.average().takeUnless { it.isNaN() }
                            val distance = ((max ?: 0) - (min ?: 0)).takeUnless { values.isEmpty() }
                            val playerLow = valuePlayers[min]?.random()
                            val playerHigh = valuePlayers[max]?.random()

                            Event.ResultUpdated(
                                selections = playerSelections,
                                statistics = Event.ResultUpdated.Statistics(
                                    min = min,
                                    max = max,
                                    average = average,
                                    median = median,
                                ),
                                discussion = Event.ResultUpdated.Discussion(
                                    distance = distance,
                                    playerLow = playerLow,
                                    playerHigh = playerHigh,
                                )
                            )
                        }

                        else -> Event.ResultUpdated(
                            selections = null,
                            statistics = null,
                        )
                    }
                )
            }
        }

    }

    companion object {
        private val logger = LoggerFactory.getLogger(RoomContext::class.java)
        private inline fun <T, R> Collection<T>?.changes(
            vararg values: T,
            block: Set<T>.() -> R,
        ) = run {
            when {
                this == null -> values.toSet().block()
                else -> filter { values.contains(it) }.toSet().takeUnless { it.isEmpty() }?.block()
            }
        }

        private fun <T> Collection<KProperty<*>>.jsonNullable(property: KProperty<T>) = run {
            when {
                contains(property) -> JsonNullable.of(property.getter.call())
                else -> JsonNullable.undefined()
            }
        }

        private fun <T, R> JsonNullable<T>.letPresent(block: (T) -> R) = when {
            isPresent -> JsonNullable.of(block(get()))
            else -> JsonNullable.undefined()
        }

        private fun <T> KMutableProperty<T>.set(
            value: JsonNullable<T>,
            dirty: MutableSet<KProperty<*>>? = null,
        ) {
            value.ifPresent { new ->
                if (getter.call() != new) {
                    setter.call(new)
                    dirty?.add(this)
                }
            }
        }

    }

}