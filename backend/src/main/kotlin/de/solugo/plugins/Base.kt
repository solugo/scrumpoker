package de.solugo.plugins

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation

fun Application.configureBase() {
    install(ContentNegotiation) {
        json()
    }
}
