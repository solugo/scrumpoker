package de.solugo.model

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer

class ContextMetrics(
    registry: MeterRegistry,
    roomCount: () -> Number,
    playerCount: () -> Number,
) {

    val roomActiveCounter = Gauge.builder("app.active", roomCount).run {
        tag("target", "room")
        register(registry)
    }
    val roomDurationTimer = Timer.builder("app.duration").run {
        tag("target", "room")
        register(registry)
    }
    val playerActiveCounter = Gauge.builder("app.active", playerCount).run {
        tag("target", "player")
        register(registry)
    }
    val playerDurationTimer = Timer.builder("app.duration").run {
        tag("target", "player")
        register(registry)
    }
    val roundStartCounter = Counter.builder("app.event").run {
        tag("target", "round")
        tag("value", "start")
        register(registry)
    }
    val roundDurationTimer = Timer.builder("app.duration").run {
        tag("target", "round")
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
    val playerKickCounter = Counter.builder("app.event").run {
        tag("target", "player")
        tag("value", "kick")
        register(registry)
    }
}