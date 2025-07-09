package de.solugo.scrumpoker.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.solugo.scrumpoker.context.RoomContext
import de.solugo.scrumpoker.model.Event
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.reactive.asPublisher
import kotlinx.coroutines.reactive.collect
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.reactive.socket.server.WebSocketService
import org.springframework.web.server.ServerWebExchange

@Controller
class RoomController(
    private val webSocketService: WebSocketService,
    private val roomContext: RoomContext,
    private val mapper: ObjectMapper,
) {

    @RequestMapping("/rooms/{roomId}/{playerId}")
    fun handle(
        @PathVariable roomId: String,
        @PathVariable playerId: String,
        exchange: ServerWebExchange,
    ) = webSocketService.handleRequest(exchange) { session ->
        channelFlow {
            try {
                println("Player $playerId joined room $roomId")
                val room = roomContext.getRoom(roomId)
                val player = room.addPlayer(playerId) {
                    send(session.textMessage(mapper.writeValueAsString(it)))
                }

                try {
                    session.receive().collect {  message ->
                        try {
                            player.process(mapper.readValue(message.payloadAsText))
                        } catch (ex: Exception) {
                            println(ex)
                            player.send(Event.Error(ex.message ?: ""))
                        }
                    }
                } finally {
                    room.removePlayer(playerId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                println("Player $playerId left room $roomId")
            }
        }.run {
            session.send(asPublisher())
        }
    }

}