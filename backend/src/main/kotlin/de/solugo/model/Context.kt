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

    suspend fun removeParticipant(participantId: String) {
        rooms.keys.forEach { roomId ->
            leaveRoom(roomId, participantId)
        }
    }

    suspend fun joinRoom(
        roomId: String?,
        participantId: String,
        name: String,
        mode: ParticipantRole = ParticipantRole.PLAYER,
        channel: SendChannel<Frame>,
    ) {
        coroutineScope {

            val actualRoomId = roomId ?: UUID.randomUUID().toString()
            val room = rooms.computeIfAbsent(actualRoomId) { Room() }

            val participant = Participant(channel = channel, name = name, role = mode)

            if (room.members.putIfAbsent(participantId, participant) == null) {
                val participantJoinedRoomEvent = participant.toParticipantJoinedRoomEvent(
                    roomId = actualRoomId,
                    participantId = participantId,
                )

                room.members.forEach { (memberId, member) ->
                    launch {
                        member.channel.sendEvent(participantJoinedRoomEvent)
                    }

                    if (memberId != participantId) {
                        launch {
                            member.channel.sendEvent(room.toRoomSelectionChangedEvent(actualRoomId, memberId))
                        }
                        launch {
                            participant.channel.sendEvent(member.toParticipantJoinedRoomEvent(actualRoomId, memberId))
                        }
                    }
                }

                launch {
                    participant.channel.sendEvent(room.toRoomSelectionChangedEvent(actualRoomId, participantId))
                }
                launch {
                    participant.channel.sendEvent(room.toRoomInfoUpdateEvent(actualRoomId))
                }
            }
        }
    }

    suspend fun leaveRoom(roomId: String, participantId: String) {
        coroutineScope {
            val room = rooms[roomId] ?: return@coroutineScope
            val participant = room.members.remove(participantId) ?: return@coroutineScope

            val participantLeftEvent = participant.toParticipantLeftRoomEvent(roomId, participantId)

            launch {
                participant.channel.sendEvent(participantLeftEvent)
            }

            room.members.forEach { (memberId, member) ->
                launch {
                    member.channel.sendEvent(participantLeftEvent)
                }
                launch {
                    member.channel.sendEvent(room.toRoomSelectionChangedEvent(roomId, memberId))
                }
            }

        }
    }

    suspend fun updateRoomInfo(
        roomId: String,
        name: String? = null,
        options: Map<String, Double?>? = null,
    ) {
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

    suspend fun updateParticipantSelection(
        roomId: String,
        participantId: String,
        selection: String?,
    ) {
        coroutineScope {
            val room = rooms[roomId] ?: return@coroutineScope
            val participant = room.members[participantId] ?: return@coroutineScope

            check(participant.role == ParticipantRole.PLAYER) { "Only players may change their selection" }

            participant.selection = selection

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


    private fun Room.toRoomSelectionChangedEvent(roomId: String, participantId: String): RoomSelectionChangedEvent {
        return RoomSelectionChangedEvent(
            roomId = roomId,
            visible = visible,
            selections = members.mapValues { (memberId, member) ->
                member.selection?.let { if (visible || memberId == participantId) it else "" }
            },
        )
    }

    private fun Participant.toParticipantJoinedRoomEvent(
        roomId: String,
        participantId: String
    ): ParticipantJoinedRoomEvent {
        return ParticipantJoinedRoomEvent(
            roomId = roomId,
            participantId = participantId,
            name = name,
            role = role,
        )
    }

    private fun Participant.toParticipantLeftRoomEvent(
        roomId: String,
        participantId: String
    ): ParticipantLeftRoomEvent {
        return ParticipantLeftRoomEvent(
            roomId = roomId,
            participantId = participantId,
        )
    }

    private data class Participant(
        val channel: SendChannel<Frame>,
        val role: ParticipantRole = ParticipantRole.PLAYER,
        val name: String,
        var selection: String? = null,
    )

    private data class Room(
        val members: MutableMap<String, Participant> = hashMapOf(),
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