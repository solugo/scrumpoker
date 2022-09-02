package de.solugo

import de.solugo.messages.event.Event
import de.solugo.messages.request.Request
import io.ktor.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.time.Duration.Companion.seconds

val json = Json {
    encodeDefaults = true
    isLenient = true
}

fun uuid() = UUID.randomUUID().toString()

val Frame.Text.request: Request; get() = json.decodeFromString(readText())

@OptIn(ExperimentalCoroutinesApi::class)
suspend inline fun SendChannel<Frame>.sendEvent(message: Event) {
    withTimeoutOrNull(1.seconds) {
        if (!isClosedForSend) {
            send(Frame.Text(json.encodeToString(message)))
        }
    }
}