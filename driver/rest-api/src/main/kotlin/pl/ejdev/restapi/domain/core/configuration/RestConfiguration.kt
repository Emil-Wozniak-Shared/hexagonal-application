package pl.ejdev.restapi.domain.core.configuration

import io.ktor.server.application.*
import pl.ejdev.restapi.infrastructure.core.configuration.configureHTTP
import pl.ejdev.restapi.infrastructure.core.configuration.configureMonitoring
import pl.ejdev.restapi.infrastructure.core.configuration.configureRouting
import pl.ejdev.restapi.infrastructure.core.configuration.configureSecurity

fun Application.restConfiguration() {
    configureRouting()
    configureHTTP()
    configureMonitoring()
    configureSecurity()
}

