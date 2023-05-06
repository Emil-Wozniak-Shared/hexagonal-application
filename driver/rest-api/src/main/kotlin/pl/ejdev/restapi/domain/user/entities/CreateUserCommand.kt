package pl.ejdev.restapi.domain.user.entities

data class CreateUserCommand(
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val roles: Set<String>
)