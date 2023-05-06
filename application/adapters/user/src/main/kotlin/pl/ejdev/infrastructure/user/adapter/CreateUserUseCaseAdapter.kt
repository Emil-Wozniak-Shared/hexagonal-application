package pl.ejdev.infrastructure.user.adapter

import arrow.core.Either
import pl.ejdev.error.BaseError
import pl.ejdev.postgres.domain.user.UserRepository
import pl.ejdev.postgres.domain.user.port.`in`.CreateUserEvent
import pl.ejdev.restapi.domain.user.entities.CreateUserCommand
import pl.ejdev.restapi.domain.user.entities.CreateUserQueryResult
import pl.ejdev.restapi.domain.user.usecases.CreateUserUseCase

class CreateUserUseCaseAdapter(
    private val userRepository: UserRepository
) : CreateUserUseCase {
    override suspend fun handle(action: CreateUserCommand): Either<BaseError, CreateUserQueryResult> =
        action
            .let { (username, firstName, lastName, email, roles) ->
                CreateUserEvent(username, firstName, lastName, email, roles)
            }
            .let { userRepository.create(it) }
            .map { (id, username, firstName, lastName, email, status, roles) ->
                CreateUserQueryResult(id, username, firstName, lastName, email, status, roles)
            }
}