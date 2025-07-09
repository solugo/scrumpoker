package de.solugo.scrumpoker.context

import de.solugo.scrumpoker.ApplicationProperties
import de.solugo.scrumpoker.model.Action
import de.solugo.scrumpoker.model.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.openapitools.jackson.nullable.JsonNullable
import org.springframework.stereotype.Component
import kotlin.reflect.KMutableProperty

@Component
class RoomContext(
    scope: CoroutineScope,
    properties: ApplicationProperties,
) {

    private val mutex = Mutex()
    private val rooms = hashMapOf<String, Room>()

    init {
        scope.launch {
            while (isActive) {
                delay(properties.room.cleanupInterval.toMillis())
                mutex.withLock {
                    rooms.keys.removeAll(
                        rooms.mapNotNull { (id, room) -> id.takeIf { room.isEmpty() } }
                    )
                }
            }
        }
    }

    fun getRoom(id: String) = rooms.computeIfAbsent(id, ::Room)

    inner class Room(
        val id: String,
    ) {
        private val mutex = Mutex()
        private val players = hashMapOf<String, Player>()

        suspend fun addPlayer(id: String, send: suspend (Event) -> Unit) = mutex.withLock {
            players.computeIfAbsent(id, ::Player).also {
                it.send = send
                players.send(Event.PlayerJoined(id))
            }
        }

        suspend fun removePlayer(id: String) = mutex.withLock {
            players.remove(id)?.also {
                players.send(Event.PlayerLeft(id))
            }
        }

        suspend fun isEmpty() = mutex.withLock {
            players.isEmpty()
        }

        inner class Player(
            val id: String,
        ) {
            var send: suspend (Event) -> Unit = {}
            var name: String? = null
            var participate: Boolean = true
            var selection: String? = null

            suspend fun process(action: Action) {
                println("Got action $action")
                var publishInfo = false
                when (action) {
                    is Action.UpdatePlayerInfo -> {
                        action.name.applyTo(::name) {
                            publishInfo = true
                        }
                        action.participate.applyTo(::participate) {
                            publishInfo = true
                        }
                        action.selection.applyTo(::selection) {
                            publishInfo = true
                        }
                    }
                }

                if (publishInfo) {
                    players.send(
                        Event.PlayerInfoUpdated(
                            id = id,
                            name = name,
                            participate = participate,
                            selection = selection,
                        )
                    )
                }

            }
        }
    }

    suspend fun Map<String, Room.Player>.send(event: Event) {
        values.forEach {
            it.send(event)
        }
    }

    fun <T> JsonNullable<T>.applyTo(property: KMutableProperty<T>, onChange: (T?) -> Unit = {}) {
        ifPresent { update ->
            val current = property.getter.call()
            if (current != update) {
                property.setter.call(update)
                onChange(update)
            }
        }
    }
}