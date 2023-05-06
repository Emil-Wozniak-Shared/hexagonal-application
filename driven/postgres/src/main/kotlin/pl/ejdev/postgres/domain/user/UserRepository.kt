package pl.ejdev.postgres.domain.user

import arrow.core.Either
import pl.ejdev.postgres.domain.user.port.`in`.CreateUserEvent
import pl.ejdev.postgres.domain.user.port.`in`.GetUserEvent
import pl.ejdev.postgres.domain.user.port.out.CreateUserResult
import pl.ejdev.postgres.domain.user.port.out.GetUserResult
import pl.ejdev.error.BaseError
import pl.ejdev.postgres.domain.user.port.`in`.GetAllUsersEvent

interface UserRepository {
    suspend fun getAll(event: GetAllUsersEvent): Either<BaseError, List<GetUserResult>>
    suspend fun get(event: GetUserEvent): Either<BaseError, GetUserResult>
    suspend fun create(event: CreateUserEvent): Either<BaseError, CreateUserResult>
}