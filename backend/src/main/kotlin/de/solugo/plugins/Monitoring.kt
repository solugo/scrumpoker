package de.solugo.plugins

import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry

fun Application.configureMonitoring() {
    val prometheus = PrometheusMeterRegistry(PrometheusConfig.DEFAULT).apply {
        Metrics.globalRegistry.add(this)
        config().meterFilter(object : MeterFilter {
            override fun configure(id: Meter.Id, config: DistributionStatisticConfig): DistributionStatisticConfig? {
                return DistributionStatisticConfig.builder().percentiles(0.25, 0.50, 0.90).build().merge(config)
            }
        })
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
