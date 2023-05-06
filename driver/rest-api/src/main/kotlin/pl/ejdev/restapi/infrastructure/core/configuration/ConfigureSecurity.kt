package pl.ejdev.restapi.infrastructure.core.configuration

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

internal fun Application.configureSecurity() {
    val config = this.environment.config
    authentication {
        jwt {
            val jwtAudience = config.property("ktor.jwt.audience").getString()
            realm = config.property("ktor.jwt.realm").getString()
            verifier(
                JWT.require(Algorithm.HMAC256("secret"))
                    .withAudience(jwtAudience)
                    .withIssuer(config.property("ktor.jwt.domain").getString())
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}