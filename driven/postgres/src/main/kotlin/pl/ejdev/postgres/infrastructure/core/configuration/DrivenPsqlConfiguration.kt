package pl.ejdev.postgres.infrastructure.core.configuration

import org.koin.dsl.module
import pl.ejdev.postgres.domain.user.UserRepository
import pl.ejdev.postgres.infrastructure.user.sources.repository.JdbcUserInfoRepository

val drivenPsqlModule = module {
    single<UserRepository> { JdbcUserInfoRepository() }
}