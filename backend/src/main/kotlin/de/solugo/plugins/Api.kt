package de.solugo.plugins

import de.solugo.messages.event.SessionStartedEvent
import de.solugo.messages.request.*
import de.solugo.model.Context
import de.solugo.request
import de.solugo.sendEvent
import de.solugo.uuid
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.supervisorScope
import org.slf4j.LoggerFactory
import java.time.Duration

fun Application.configureApi() {

    val context = Context()
    val logger = LoggerFactory.getLogger("api")

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(1)
        timeout = Duration.ofSeconds(1)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/api") {
            val playerId = uuid()

            try {
                logger.info("Player $playerId joined")

                supervisorScope {
                    outgoing.sendEvent(
                        SessionStartedEvent(
                            playerId = playerId,
                        )
                    )

                    for (frame in incoming) {
                        try {
                            when (frame) {
                                is Frame.Text -> when (val request = frame.request) {
                                    is UpdatePlayerInfoRequest -> context.updatePlayerInfo(
                                        roomId = request.roomId,
                                        playerId = playerId,
                                        name = request.name,
                                    )
                                    is JoinRoomRequest -> context.joinRoom(
                                        roomId = request.roomId,
                                        playerId = playerId,
                                        channel = outgoing,
                                    )
                                    is LeaveRoomRequest -> context.leaveRoom(
                                        roomId = request.roomId,
                                        playerId = playerId,
                                    )
                                    is UpdateSelectionRequest -> context.updatePlayerSelection(
                                        roomId = request.roomId,
                                        playerId = playerId,
                                        selection = request.selection,
                                    )
                                    is ResetRoomRequest -> context.resetRoom(
                                        roomId = request.roomId,
                                    )
                                    is UpdateRoomInfoRequest -> context.updateRoomInfo(
                                        roomId = request.roomId,
                                        name = request.name,
                                        options = request.options,
                                    )

                                    is RevealRoomRequest -> context.revealRoom(
                                        roomId = request.roomId,
                                        visible = request.visible,
                                    )

                                }
                                is Frame.Close -> {
                                    logger.info("Player $playerId closed")
                                }
                                else -> {
                                    logger.info("Could not process frame type: ${frame.frameType}")
                                }
                            }
                        } catch (ex: Exception) {
                            logger.error("Could not process frame: $frame", ex)
                        }
                    }
                }
            } finally {
                context.removePlayer(playerId)
                logger.info("Player $playerId left")
            }
        }
    }

}
