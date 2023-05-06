package pl.ejdev.infrastructure.core.configuration

import io.ktor.server.application.*
import org.koin.core.logger.PrintLogger
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import pl.ejdev.infrastructure.user.configuration.userInfrastructureModule
import pl.ejdev.postgres.domain.user.configuration.userRepositoryModule
import pl.ejdev.postgres.infrastructure.core.configuration.drivenPsqlModule
import pl.ejdev.restapi.domain.core.configuration.userRestApiModule

internal fun Application.modulesConfiguration() = module {
    install(Koin) {
        logger(PrintLogger())
        modules(
            userRepositoryModule,
            userInfrastructureModule,
            drivenPsqlModule,
            userRestApiModule,
        )
    }
}