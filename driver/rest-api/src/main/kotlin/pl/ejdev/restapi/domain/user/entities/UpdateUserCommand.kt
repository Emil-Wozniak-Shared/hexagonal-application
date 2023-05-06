package pl.ejdev.restapi.domain.user.entities

class UpdateUserCommand(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val roles: Set<String>
)