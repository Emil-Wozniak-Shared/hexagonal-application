package pl.ejdev.postgres.domain.user.configuration

import org.koin.dsl.module
import pl.ejdev.postgres.domain.user.UserRepository
import pl.ejdev.postgres.infrastructure.user.sources.repository.JdbcUserInfoRepository

val userRepositoryModule = module {
    single<UserRepository> { JdbcUserInfoRepository() }
}