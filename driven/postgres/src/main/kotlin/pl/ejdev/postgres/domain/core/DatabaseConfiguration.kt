package pl.ejdev.postgres.domain.core

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import pl.ejdev.postgres.infrastructure.user.sources.repository.UserTable

fun Application.configureDatabases() {
    Database.connect(
        url = this variable "database.url",
        driver = this variable "database.driver",
        user = this variable "database.user",
        password = this variable "database.password",
    )
    transaction {
        SchemaUtils.create(UserTable)
//        SchemaUtils.create(PostTable)
    }
}

private inline infix fun <reified T> Application.variable(prop: String): T =
    this.environment.config.property("ktor.${prop}").run {
        when (T::class.java.name) {
            String::class.java.name -> getString() as T
            List::class.java.name -> getList() as T
            else -> throw IllegalArgumentException()
        }
    }