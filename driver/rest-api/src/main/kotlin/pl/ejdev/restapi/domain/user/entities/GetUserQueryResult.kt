package pl.ejdev.restapi.domain.user.entities

class GetUserQueryResult(
        val id: Long,
        val username: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val status: String,
        val roles: Set<String>
)