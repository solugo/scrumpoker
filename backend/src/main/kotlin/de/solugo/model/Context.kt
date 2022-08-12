package de.solugo.model

import de.solugo.messages.event.*
import de.solugo.sendEvent
import io.ktor.websocket.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*

class Context {
    private val rooms = hashMapOf<String, Room>()

    suspend fun removePlayer(playerId: String) {
        rooms.keys.forEach { roomId ->
            leaveRoom(roomId, playerId)
        }
    }

    suspend fun joinRoom(roomId: String?, playerId: String, channel: SendChannel<Frame>) {
        coroutineScope {

            val actualRoomId = roomId ?: UUID.randomUUID().toString()
            val room = rooms.computeIfAbsent(actualRoomId) { Room() }

            val player = Player(channel = channel)
            if (room.members.putIfAbsent(playerId, player) == null) {
                val playerJoinedRoomEvent = player.toPlayerJoinedRoomEvent(
                    roomId = actualRoomId,
                    playerId = playerId,
                )
                val playerInfoUpdatedEvent = player.toPlayerInfoUpdateEvent(
                    roomId = actualRoomId,
                    playerId = playerId,
                )

                room.members.forEach { (memberId, member) ->
                    launch {
                        member.channel.sendEvent(playerJoinedRoomEvent)
                    }
                    launch {
                        member.channel.sendEvent(playerInfoUpdatedEvent)
                    }

                    if (memberId != playerId) {
                        launch {
                            member.channel.sendEvent(room.toRoomSelectionChangedEvent(actualRoomId, memberId))
                        }
                        launch {
                            player.channel.sendEvent(member.toPlayerJoinedRoomEvent(actualRoomId, memberId))
                        }
                        launch {
                            player.channel.sendEvent(member.toPlayerInfoUpdateEvent(actualRoomId, memberId))
                        }
                    }
                }

                launch {
                    player.channel.sendEvent(room.toRoomSelectionChangedEvent(actualRoomId, playerId))
                }
                launch {
                    player.channel.sendEvent(room.toRoomInfoUpdateEvent(actualRoomId))
                }
            }
        }
    }

    suspend fun leaveRoom(roomId: String, playerId: String) {
        coroutineScope {
            val room = rooms[roomId] ?: return@coroutineScope
            val player = room.members.remove(playerId) ?: return@coroutineScope

            val playerLeftEvent = player.toPlayerLeftRoomEvent(roomId, playerId)

            launch {
                player.channel.sendEvent(playerLeftEvent)
            }

            room.members.forEach { (memberId, member) ->
                launch {
                    member.channel.sendEvent(playerLeftEvent)
                }
                launch {
                    member.channel.sendEvent(room.toRoomSelectionChangedEvent(roomId, memberId))
                }
            }

        }
    }

    suspend fun updateRoomInfo(roomId: String, name: String? = null, options: Map<String, Double?>? = null) {
        coroutineScope {
            val room = rooms[roomId] ?: return@coroutineScope

            name?.takeUnless { it == room.name }?.also {
                room.name = it
            }
            options?.takeUnless { it == room.options }?.also {
                room.options = it
                room.members.values.forEach { member ->
                    member.selection = null
                }

                room.members.forEach { (memberId, member) ->
                    launch {
                        member.channel.sendEvent(room.toRoomSelectionChangedEvent(roomId, memberId))
                    }
                }
            }

            val roomInfoUpdatedEvent = room.toRoomInfoUpdateEvent(roomId)

            room.members.values.forEach { member ->
                launch {
                    member.channel.sendEvent(roomInfoUpdatedEvent)
                }
            }
        }

    }

    suspend fun updatePlayerInfo(roomId: String, playerId: String, name: String?) {
        coroutineScope {
            val room = rooms[roomId] ?: return@coroutineScope
            val player = room.members[playerId] ?: return@coroutineScope

            name?.also { player.name = it }

            room.members.values.forEach { member ->
                launch {
                    member.channel.sendEvent(
                        PlayerInfoChangedEvent(roomId = roomId, playerId = playerId, name = player.name)
                    )
                }

            }
        }
    }

    suspend fun updatePlayerSelection(roomId: String, playerId: String, selection: String?) {
        coroutineScope {
            val room = rooms[roomId] ?: return@coroutineScope
            val member = room.members[playerId] ?: return@coroutineScope

            member.selection = selection

            room.members.forEach { (memberId, member) ->
                launch {
                    member.channel.sendEvent(room.toRoomSelectionChangedEvent(roomId, memberId))
                }
            }
        }
    }

    suspend fun resetRoom(roomId: String) {
        coroutineScope {

            val room = rooms[roomId] ?: return@coroutineScope
            room.visible = false

            room.members.values.forEach { member ->
                member.selection = null
            }

            room.members.forEach { (memberId, member) ->
                launch { member.channel.sendEvent(room.toRoomSelectionChangedEvent(roomId, memberId)) }
            }
        }

    }

    suspend fun revealRoom(roomId: String, visible: Boolean) {
        coroutineScope {

            val room = rooms[roomId] ?: return@coroutineScope
            room.visible = visible

            room.members.forEach { (memberId, member) ->
                launch { member.channel.sendEvent(room.toRoomSelectionChangedEvent(roomId, memberId)) }
            }
        }

    }

    private fun Room.toRoomInfoUpdateEvent(roomId: String): RoomInfoChangedEvent {
        return RoomInfoChangedEvent(
            roomId = roomId,
            name = name,
            options = options,
        )
    }


    private fun Room.toRoomSelectionChangedEvent(roomId: String, playerId: String): RoomSelectionChangedEvent {
        return RoomSelectionChangedEvent(
            roomId = roomId,
            visible = visible,
            selections = members.mapValues { (memberId, member) ->
                member.selection?.let { if (visible || memberId == playerId) it else "" }
            },
        )
    }

    private fun Player.toPlayerJoinedRoomEvent(roomId: String, playerId: String): PlayerJoinedRoomEvent {
        return PlayerJoinedRoomEvent(
            roomId = roomId,
            playerId = playerId,
        )
    }

    private fun Player.toPlayerLeftRoomEvent(roomId: String, playerId: String): PlayerLeftRoomEvent {
        return PlayerLeftRoomEvent(
            roomId = roomId,
            playerId = playerId,
        )
    }

    private fun Player.toPlayerInfoUpdateEvent(roomId: String, playerId: String): PlayerInfoChangedEvent {
        return PlayerInfoChangedEvent(
            roomId = roomId,
            playerId = playerId,
            name = name,
        )
    }


    private data class Player(
        val channel: SendChannel<Frame>,
        var name: String? = null,
        var selection: String? = null,
    )

    private data class Room(
        val members: MutableMap<String, Player> = hashMapOf(),
        var name: String? = null,
        var visible: Boolean = false,
        var options: Map<String, Double?> = mapOf(
            "½" to 0.5,
            "1" to 1.0,
            "2" to 2.0,
            "3" to 3.0,
            "5" to 5.0,
            "8" to 8.0,
            "13" to 13.0,
            "21" to 21.0,
            "♾" to null,
            "☕" to null,
            "⚡" to null,
        ),
    )

}