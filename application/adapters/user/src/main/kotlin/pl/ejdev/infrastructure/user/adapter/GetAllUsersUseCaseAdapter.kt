package pl.ejdev.infrastructure.user.adapter

import arrow.core.Either
import pl.ejdev.error.BaseError
import pl.ejdev.postgres.domain.user.UserRepository
import pl.ejdev.postgres.domain.user.port.`in`.GetAllUsersEvent
import pl.ejdev.restapi.domain.user.entities.GetAllUserQuery
import pl.ejdev.restapi.domain.user.entities.GetUserQueryResult
import pl.ejdev.restapi.domain.user.usecases.GetAllUsersUseCase

class GetAllUsersUseCaseAdapter(
    private val userRepository: UserRepository
) : GetAllUsersUseCase {

    override suspend fun handle(action: GetAllUserQuery): Either<BaseError, List<GetUserQueryResult>> =
        GetAllUsersEvent
            .let { userRepository.getAll(it) }
            .map {
                it.map { (id, username, firstName, lastName, email, status, roles) ->
                    GetUserQueryResult(id, username, firstName, lastName, email, status, roles)
                }
            }
}