package pl.ejdev.restapi.infrastructure.user

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import pl.ejdev.restapi.domain.user.entities.CreateUserCommand
import pl.ejdev.restapi.domain.user.entities.GetAllUserQuery
import pl.ejdev.restapi.domain.user.entities.GetUserQuery
import pl.ejdev.restapi.domain.user.usecases.CreateUserUseCase
import pl.ejdev.restapi.domain.user.usecases.GetAllUsersUseCase
import pl.ejdev.restapi.domain.user.usecases.GetUserUseCase
import pl.ejdev.restapi.infrastructure.core.utils.paramId
import pl.ejdev.restapi.infrastructure.core.utils.respond
import pl.ejdev.restapi.infrastructure.user.models.CreateUser
import pl.ejdev.restapi.infrastructure.user.models.toNames

const val PATH = "/users"

internal fun Route.users() {
    val getUserUseCase by inject<GetUserUseCase>()
    val getAllUsersUseCase by inject<GetAllUsersUseCase>()
    val createUserUseCase by inject<CreateUserUseCase>()

    route(PATH) {
        get {
            getAllUsersUseCase.handle(GetAllUserQuery)
                .respond(call)
        }
        get("/{id}") {
            GetUserQuery(paramId)
                .let { getUserUseCase.handle(it) }
                .respond(call)
        }
        post {
            call.receive<CreateUser>()
                .let { (username, firstName, lastName, email, roles) ->
                    CreateUserCommand(username, firstName, lastName, email, roles.toNames())
                }
                .let { createUserUseCase.handle(it) }
                .respond(call)
        }
    }
}