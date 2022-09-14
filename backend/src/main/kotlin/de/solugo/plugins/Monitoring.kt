package de.solugo.plugins

import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.core.instrument.Metrics
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry

fun Application.configureMonitoring() {
    val prometheus = PrometheusMeterRegistry(PrometheusConfig.DEFAULT).also {
        Metrics.globalRegistry.add(it)
    }

    install(MicrometerMetrics) {
        registry = prometheus
    }

    routing {
        get("/metrics") {
            call.respond(prometheus.scrape())
        }
    }
}
