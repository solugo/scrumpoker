package de.solugo.model

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry

class ContextMetrics(
    registry: MeterRegistry,
    roomCount: () -> Number,
    playerCount: () -> Number,
) {

    val roomActiveCounter = Gauge.builder("app.count", roomCount).run {
        tag("value", "room")
        register(registry)
    }
    val playerActiveCounter = Gauge.builder("app.count", playerCount).run {
        tag("value", "player")
        register(registry)
    }
    val roundStartCounter = Counter.builder("app.event").run {
        tag("target", "round")
        tag("value", "start")
        register(registry)
    }
    val roundRevealCounter = Counter.builder("app.event").run {
        tag("target", "round")
        tag("value", "reveal")
        register(registry)
    }
    val roundHideCounter = Counter.builder("app.event").run {
        tag("target", "round")
        tag("value", "hide")
        register(registry)
    }
    val roomCreateCounter = Counter.builder("app.event").run {
        tag("target", "room")
        tag("value", "create")
        register(registry)
    }
    val roomRemoveCounter = Counter.builder("app.event").run {
        tag("target", "room")
        tag("value", "remove")
        register(registry)
    }
    val playerJoinCounter = Counter.builder("app.event").run {
        tag("target", "player")
        tag("value", "join")
        register(registry)
    }
    val playerLeaveCounter = Counter.builder("app.event").run {
        tag("target", "player")
        tag("value", "join")
        register(registry)
    }
    val playerSelectCounter = Counter.builder("app.event").run {
        tag("target", "player")
        tag("value", "select")
        register(registry)
    }
}