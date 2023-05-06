package pl.ejdev.restapi.domain.user.usecases

import arrow.core.Either
import pl.ejdev.restapi.domain.core.usecases.UseCase
import pl.ejdev.error.BaseError
import pl.ejdev.restapi.domain.user.entities.CreateUserCommand
import pl.ejdev.restapi.domain.user.entities.CreateUserQueryResult

interface CreateUserUseCase : UseCase<CreateUserCommand, CreateUserQueryResult> {
    override suspend fun handle(action: CreateUserCommand): Either<BaseError, CreateUserQueryResult>
}