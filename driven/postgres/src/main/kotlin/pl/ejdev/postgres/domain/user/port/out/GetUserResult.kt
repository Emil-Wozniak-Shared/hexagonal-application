package pl.ejdev.postgres.domain.user.port.out

data class GetUserResult(
        val id: Long,
        val username: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val status: String,
        val roles: Set<String>
)