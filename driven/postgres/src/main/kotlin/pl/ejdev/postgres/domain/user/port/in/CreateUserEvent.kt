package pl.ejdev.postgres.domain.user.port.`in`

class CreateUserEvent(
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val roles: Set<String>
)