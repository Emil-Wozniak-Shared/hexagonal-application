package pl.ejdev.restapi.infrastructure.core.problem

import java.time.LocalDateTime

/**
 * Problem details response based on [RFC 7807](https://www.rfc-editor.org/rfc/rfc7807)
 */
data class ProblemDetails(
    val error: String,
    val message: String,
    val path: String? = null,
    val status: Int = 400,
    val timestamp: LocalDateTime = LocalDateTime.now(),
)