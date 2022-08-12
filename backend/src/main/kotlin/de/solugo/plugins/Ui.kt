package de.solugo.plugins

import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*

fun Application.configureUi() {
    routing {
        static {
            files("./frontend")
            default("./frontend/index.html")
        }
    }
}
