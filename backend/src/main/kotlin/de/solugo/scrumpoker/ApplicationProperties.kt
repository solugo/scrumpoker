package de.solugo.scrumpoker

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "service")
data class ApplicationProperties(
    val room: Room = Room(),
) {
    data class Room(
        val cleanupInterval: Duration = Duration.ofMinutes(1),
    )
}