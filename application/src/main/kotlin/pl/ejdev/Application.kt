package pl.ejdev

import io.ktor.server.application.*
import io.ktor.server.cio.*
import pl.ejdev.infrastructure.core.configuration.modulesConfiguration
import pl.ejdev.postgres.domain.core.configureDatabases
import pl.ejdev.restapi.domain.core.configuration.restConfiguration

fun main(args: Array<String>): Unit =
    EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureDatabases()
    modulesConfiguration()
    restConfiguration()
}
