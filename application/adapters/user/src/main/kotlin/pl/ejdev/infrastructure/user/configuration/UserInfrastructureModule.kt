package pl.ejdev.infrastructure.user.configuration

import org.koin.core.module.Module
import org.koin.dsl.module
import pl.ejdev.infrastructure.user.adapter.CreateUserUseCaseAdapter
import pl.ejdev.infrastructure.user.adapter.GetAllUsersUseCaseAdapter
import pl.ejdev.infrastructure.user.adapter.GetUserUseCaseAdapter
import pl.ejdev.restapi.domain.user.usecases.CreateUserUseCase
import pl.ejdev.restapi.domain.user.usecases.GetAllUsersUseCase
import pl.ejdev.restapi.domain.user.usecases.GetUserUseCase

val userInfrastructureModule: Module = module {
    single<GetUserUseCase> { GetUserUseCaseAdapter(get()) }
    single<GetAllUsersUseCase> { GetAllUsersUseCaseAdapter(get()) }
    single<CreateUserUseCase> { CreateUserUseCaseAdapter(get()) }
}