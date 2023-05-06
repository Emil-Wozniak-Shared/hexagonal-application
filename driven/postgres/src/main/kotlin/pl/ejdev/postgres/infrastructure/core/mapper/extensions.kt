package pl.ejdev.postgres.infrastructure.core.mapper

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.IColumnType
import org.jetbrains.exposed.sql.ResultRow
import pl.ejdev.postgres.infrastructure.user.sources.repository.UserTable

internal fun <T> ResultRow.column(column: Column<T>): T =
    get(Column(UserTable, column.name, column.columnType))


internal fun <T> ResultRow.column(name: String, columnType: IColumnType): T =
    get(Column(UserTable, name, columnType))