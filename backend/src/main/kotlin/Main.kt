@file:JvmName("ScrumPoker")

import de.solugo.plugins.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0") {
        configureMonitoring()
        configureApi()
        configureUi()
    }.start(
        wait = true,
    )
}