package pl.ejdev.postgres.infrastructure.user.sources.repository

import arrow.core.Either
import arrow.core.flatten
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.selectAllBatched
import org.jetbrains.exposed.sql.selectBatched
import pl.ejdev.error.BaseError
import pl.ejdev.error.MustBeNonNullError
import pl.ejdev.postgres.domain.user.UserRepository
import pl.ejdev.postgres.domain.user.port.`in`.CreateUserEvent
import pl.ejdev.postgres.domain.user.port.`in`.GetAllUsersEvent
import pl.ejdev.postgres.domain.user.port.`in`.GetUserEvent
import pl.ejdev.postgres.domain.user.port.out.CreateUserResult
import pl.ejdev.postgres.domain.user.port.out.GetUserResult
import pl.ejdev.postgres.infrastructure.core.utils.mapNotNull
import pl.ejdev.postgres.infrastructure.core.utils.query
import pl.ejdev.postgres.infrastructure.core.utils.rightIsNotNull
import pl.ejdev.postgres.infrastructure.user.mapper.UserResultRowMapper

private const val USER_NOT_FOUND = "User not found"

internal class JdbcUserInfoRepository : UserRepository {
    override suspend fun getAll(event: GetAllUsersEvent): Either<BaseError, List<GetUserResult>> =
        query {
            UserTable
                .selectAllBatched(10)
                .flatMap { it.map(UserResultRowMapper::toGetUserResult) }
        }

    override suspend fun get(event: GetUserEvent): Either<BaseError, GetUserResult> =
        query {
            UserTable.selectBatched { UserTable.id eq event.id }
                .flatMap { it.map(UserResultRowMapper::toGetUserResult) }
                .firstOrNull()
        }.mapNotNull(::rightIsNotNull) {
            MustBeNonNullError(
                error = USER_NOT_FOUND,
                message = "User with id: ${event.id} not found",
            )
        }

    override suspend fun create(event: CreateUserEvent): Either<BaseError, CreateUserResult> =
        query {
            UserTable.insert {
                it[id] = (UserTable.selectAll().count() + 1)
                it[username] = event.username
                it[firstName] = event.firstName
                it[lastName] = event.lastName
                it[email] = event.email
                it[password] = ""
                it[phone] = ""
                it[status] = Status.ACTIVE.name
                it[roles] = event.roles.toTypedArray()
            }[UserTable.id]
        }.map(::GetUserEvent)
            .map { get(it) }
            .flatten()
            .map { (id, username, firstName, lastName, email, status, roles) ->
                CreateUserResult(id, username, firstName, lastName, email, status, roles)
            }
}


