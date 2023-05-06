package pl.ejdev.infrastructure.user.adapter

import arrow.core.Either
import pl.ejdev.error.BaseError
import pl.ejdev.postgres.domain.user.UserRepository
import pl.ejdev.postgres.domain.user.port.`in`.GetUserEvent
import pl.ejdev.restapi.domain.user.entities.GetUserQuery
import pl.ejdev.restapi.domain.user.entities.GetUserQueryResult
import pl.ejdev.restapi.domain.user.usecases.GetUserUseCase

class GetUserUseCaseAdapter(
    private val userRepository: UserRepository
) : GetUserUseCase {
    override suspend fun handle(action: GetUserQuery): Either<BaseError, GetUserQueryResult> =
        GetUserEvent(action.id)
            .let { userRepository.get(it) }
            .map { (id, username, firstName, lastName, email, status, roles) ->
                GetUserQueryResult(id, username, firstName, lastName, email, status, roles)
            }
}