package pl.ejdev.postgres.infrastructure.user.mapper

import org.jetbrains.exposed.sql.ResultRow
import pl.ejdev.postgres.domain.user.port.out.GetUserResult
import pl.ejdev.postgres.infrastructure.core.mapper.column
import pl.ejdev.postgres.infrastructure.user.sources.repository.UserTable

internal object UserResultRowMapper {
    fun toGetUserResult(row: ResultRow): GetUserResult =
        row.run {
            GetUserResult(
                id = column(UserTable.id),
                username = column(UserTable.username),
                firstName = column(UserTable.firstName),
                lastName = column(UserTable.lastName),
                email = column(UserTable.email),
                status = column(UserTable.status),
                roles = column<Array<Any>>(UserTable.roles.name, UserTable.roles.columnType)
                    .filterIsInstance<String>()
                    .toSet(),
            )
        }
}

