package pl.ejdev.restapi.infrastructure.core.configuration

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pl.ejdev.restapi.infrastructure.user.users

internal fun Application.configureRouting() {
    install(ContentNegotiation) {
        jackson { enable(SerializationFeature.INDENT_OUTPUT) }
    }
    routing {
        get("/") {
            call.respondText("Server is alive")
        }
        route("/api") {
            users()
        }
    }
}