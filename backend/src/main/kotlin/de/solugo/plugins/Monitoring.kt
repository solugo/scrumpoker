package de.solugo.plugins

import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry

object MetricsRegistry : PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

fun Application.configureMetrics() {
    install(MicrometerMetrics) {
        registry = MetricsRegistry
    }

    routing {
        get("/metrics") {
            call.respond(MetricsRegistry.scrape())
        }
    }
}
