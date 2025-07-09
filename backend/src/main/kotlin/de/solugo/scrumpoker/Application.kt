package de.solugo.scrumpoker

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.openapitools.jackson.nullable.JsonNullableModule
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.event.ContextStoppedEvent
import org.springframework.context.event.EventListener
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties::class)
class Application {

    @Bean
    fun scope() = run {
        CoroutineScope(SupervisorJob())
    }

    @Bean
    fun jsonNullableModule() = run {
        JsonNullableModule()
    }

    @Bean
    fun webSocketService() = run {
        HandshakeWebSocketService()
    }

    @EventListener(ContextStoppedEvent::class)
    fun onClose(scope: CoroutineScope) {
        scope.cancel()
    }

}