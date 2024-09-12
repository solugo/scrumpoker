package de.solugo.model

import de.solugo.messages.event.*
import de.solugo.plugins.MetricsRegistry
import de.solugo.sendEvent
import io.ktor.websocket.*
import io.micrometer.core.instrument.Metrics
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.System.currentTimeMillis
import java.util.*
import java.util.concurrent.TimeUnit

class Context {
    private val rooms = hashMapOf<String, Room>()

    private val metrics = ContextMetrics(
        roomCount = { rooms.size },
        playerCount = { rooms.values.sumOf { it.members.size } },
    )

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
            val room = rooms.computeIfAbsent(actualRoomId) {
                metrics.roomCreateCounter.increment()
                Room()
            }

            val participant = Participant(channel = channel, name = name, role = mode)

            metrics.playerJoinCounter.increment()

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

            metrics.playerLeaveCounter.increment()

            val room = rooms[roomId] ?: return@coroutineScope
            val participant = room.members.remove(participantId) ?: return@coroutineScope

            metrics.playerDurationTimer.record(currentTimeMillis() - participant.created, TimeUnit.MILLISECONDS)

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

            if (room.members.isEmpty()) {

                metrics.roomRemoveCounter.increment()
                metrics.roomDurationTimer.record(currentTimeMillis() - room.created, TimeUnit.MILLISECONDS)
                metrics.roundDurationTimer.record(currentTimeMillis() - room.round.created, TimeUnit.MILLISECONDS)
                rooms.remove(roomId)
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
                room.round.selections.clear()

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

            metrics.playerSelectCounter.increment()

            val room = rooms[roomId] ?: return@coroutineScope
            val participant = room.members[participantId] ?: return@coroutineScope

            check(participant.role == ParticipantRole.PLAYER) { "Only players may change their selection" }


            room.round.selections[participantId] = selection

            room.members.forEach { (memberId, member) ->
                launch {
                    member.channel.sendEvent(room.toRoomSelectionChangedEvent(roomId, memberId))
                }
            }
        }
    }

    suspend fun resetRoom(roomId: String) {
        coroutineScope {

            metrics.roundStartCounter.increment()

            val room = rooms[roomId] ?: return@coroutineScope

            metrics.roundDurationTimer.record(currentTimeMillis() - room.round.created, TimeUnit.MILLISECONDS)

            room.round = Round()

            room.members.forEach { (memberId, member) ->
                launch { member.channel.sendEvent(room.toRoomSelectionChangedEvent(roomId, memberId)) }
            }
        }

    }

    suspend fun revealRoom(roomId: String, visible: Boolean) {
        coroutineScope {

            when (visible) {
                true -> metrics.roundRevealCounter.increment()
                else -> metrics.roundHideCounter.increment()
            }

            val room = rooms[roomId] ?: return@coroutineScope
            room.round.visible = visible

            room.members.forEach { (memberId, member) ->
                launch { member.channel.sendEvent(room.toRoomSelectionChangedEvent(roomId, memberId)) }
            }
        }

    }

    suspend fun removeRoom(roomId: String) {
        coroutineScope {
            val room = rooms[roomId] ?: return@coroutineScope
            metrics.roomRemoveCounter.increment()
            metrics.roomDurationTimer.record(currentTimeMillis() - room.created, TimeUnit.MILLISECONDS)
            metrics.roundDurationTimer.record(currentTimeMillis() - room.round.created, TimeUnit.MILLISECONDS)
            rooms.remove(roomId)
        }
    }

    suspend fun kickParticipant(roomId: String, participantId: String, initiatorParticipantId: String) {
        coroutineScope {
            val room = rooms[roomId] ?: return@coroutineScope
            val participant = room.members.remove(participantId) ?: return@coroutineScope
            metrics.playerKickCounter.increment()
            val participantKickEvent =
                participant.toParticipantKickEvent(roomId, participantId, initiatorParticipantId)
            participant.channel.sendEvent(participantKickEvent)
            room.members.forEach{(memberId, member) ->
                launch {
                    member.channel.sendEvent(participantKickEvent)
                }
                launch {
                    member.channel.sendEvent(room.toRoomSelectionChangedEvent(roomId, memberId))
                }
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
            visible = round.visible,
            selections = members.keys.associateWith { memberId ->
                round.selections[memberId]?.let { if (round.visible || memberId == participantId) it else "" }
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

    private fun Participant.toParticipantKickEvent(
        roomId: String,
        participantId: String,
        initiatorParticipantId: String
    ): ParticipantKickedEvent {
        return ParticipantKickedEvent(
            roomId = roomId,
            participantId = participantId,
            initiatorParticipantId = initiatorParticipantId
        )
    }

    private data class Participant(
        val created: Long = currentTimeMillis(),
        val channel: SendChannel<Frame>,
        val role: ParticipantRole = ParticipantRole.PLAYER,
        val name: String,
    )

    private data class Room(
        val created: Long = currentTimeMillis(),
        val members: MutableMap<String, Participant> = hashMapOf(),
        var name: String? = null,
        var round: Round = Round(),
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

    private data class Round(
        val created: Long = currentTimeMillis(),
        val selections: MutableMap<String, String?> = hashMapOf(),
        var visible: Boolean = false,
    )

}