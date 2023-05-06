package pl.ejdev.postgres.infrastructure.user.sources.repository

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.VarCharColumnType
import pl.ejdev.postgres.infrastructure.core.utils.array

enum class Status {
    ACTIVE, INACTIVE, BLOCKED;
}

object UserTable : Table() {
    val id = long("id").autoIncrement()
    val username = varchar("username", length = 50)
    val firstName = varchar("first_name", length = 50)
    val lastName = varchar("last_name", length = 50)
    val email = varchar("email", length = 50)
    val password = varchar("password", length = 50)
    val phone = varchar("phone", length = 50)
    val status = varchar("status", length = 50)
    val roles = array("roles", VarCharColumnType())

    override val primaryKey = PrimaryKey(id)
}
