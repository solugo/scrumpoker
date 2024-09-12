package de.solugo.plugins

import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*
import java.io.File

fun Application.configureUi() {
    routing {
        staticFiles("/", File("frontend"), index = "index.html")
    }
}
