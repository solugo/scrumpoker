package de.solugo.model

import de.solugo.plugins.MetricsRegistry
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer

@Suppress("unused")
class ContextMetrics(
    roomCount: () -> Number,
    playerCount: () -> Number,
) {

    val roomActiveCounter = Gauge.builder("app.active", roomCount).run {
        tag("target", "room")
        register(MetricsRegistry)
    }
    val roomDurationTimer = Timer.builder("app.duration").run {
        tag("target", "room")
        register(MetricsRegistry)
    }
    val playerActiveCounter = Gauge.builder("app.active", playerCount).run {
        tag("target", "player")
        register(MetricsRegistry)
    }
    val playerDurationTimer = Timer.builder("app.duration").run {
        tag("target", "player")
        register(MetricsRegistry)
    }
    val roundStartCounter = Counter.builder("app.event").run {
        tag("target", "round")
        tag("value", "start")
        register(MetricsRegistry)
    }
    val roundDurationTimer = Timer.builder("app.duration").run {
        tag("target", "round")
        register(MetricsRegistry)
    }
    val roundRevealCounter = Counter.builder("app.event").run {
        tag("target", "round")
        tag("value", "reveal")
        register(MetricsRegistry)
    }
    val roundHideCounter = Counter.builder("app.event").run {
        tag("target", "round")
        tag("value", "hide")
        register(MetricsRegistry)
    }
    val roomCreateCounter = Counter.builder("app.event").run {
        tag("target", "room")
        tag("value", "create")
        register(MetricsRegistry)
    }
    val roomRemoveCounter = Counter.builder("app.event").run {
        tag("target", "room")
        tag("value", "remove")
        register(MetricsRegistry)
    }
    val playerJoinCounter = Counter.builder("app.event").run {
        tag("target", "player")
        tag("value", "join")
        register(MetricsRegistry)
    }
    val playerLeaveCounter = Counter.builder("app.event").run {
        tag("target", "player")
        tag("value", "join")
        register(MetricsRegistry)
    }
    val playerSelectCounter = Counter.builder("app.event").run {
        tag("target", "player")
        tag("value", "select")
        register(MetricsRegistry)
    }
    val playerKickCounter = Counter.builder("app.event").run {
        tag("target", "player")
        tag("value", "kick")
        register(MetricsRegistry)
    }
}