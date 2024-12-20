package de.solugo.plugins

import de.solugo.messages.event.ErrorEvent
import de.solugo.messages.event.SessionStartedEvent
import de.solugo.messages.request.*
import de.solugo.model.Context
import de.solugo.request
import de.solugo.sendEvent
import de.solugo.uuid
import io.ktor.server.application.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.supervisorScope
import org.slf4j.LoggerFactory
import java.util.Properties
import kotlin.time.Duration.Companion.seconds

fun Application.configureApi() {

    val context = Context()
    val logger = LoggerFactory.getLogger("api")
    val buildInfo = Properties().apply {
        ClassLoader.getSystemClassLoader().getResourceAsStream("META-INF/build-info.properties")?.also {
            load(it)
        }
    }

    install(WebSockets) {
        pingPeriod = 1.seconds
        timeout = 1.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        route("/api") {
            get("/health") {
                call.respond("UP")
            }
            get("/version") {
                call.respond(buildInfo)
            }

            webSocket {
                val participantId = uuid()

                try {
                    logger.info("Participant $participantId joined")

                    supervisorScope {
                        outgoing.sendEvent(
                            SessionStartedEvent(
                                participantId = participantId,
                            )
                        )

                        for (frame in incoming) {
                            try {
                                when (frame) {
                                    is Frame.Text -> when (val request = frame.request) {
                                        is JoinRoomRequest -> context.joinRoom(
                                            roomId = request.roomId,
                                            participantId = participantId,
                                            channel = outgoing,
                                            name = request.name,
                                            mode = request.role,
                                        )
                                        is LeaveRoomRequest -> context.leaveRoom(
                                            roomId = request.roomId,
                                            participantId = participantId,
                                        )
                                        is UpdateSelectionRequest -> context.updateParticipantSelection(
                                            roomId = request.roomId,
                                            participantId = participantId,
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
                                        is KickParticipantRequest -> context.kickParticipant(
                                            roomId = request.roomId,
                                            participantId = request.participantId,
                                            initiatorParticipantId = participantId
                                        )

                                    }
                                    is Frame.Close -> {
                                        logger.info("Participant $participantId closed")
                                    }
                                    else -> {
                                        logger.info("Could not process frame type: ${frame.frameType}")
                                    }
                                }
                            } catch (ex: Exception) {
                                outgoing.sendEvent(ErrorEvent(ex.message ?: "Unknown error"))
                                logger.error("Could not process frame: $frame", ex)
                            }
                        }
                    }
                } finally {
                    context.removeParticipant(participantId)
                    logger.info("Participant $participantId left")
                }
            }
        }

    }

}
