package pl.ejdev.restapi.infrastructure.user.models

data class CreateUser(
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val roles: Set<Role> = setOf(),
)
