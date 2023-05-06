package pl.ejdev.restapi.infrastructure.core.utils

import arrow.core.Either
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import pl.ejdev.error.BaseError
import pl.ejdev.restapi.infrastructure.core.problem.ProblemDetails

internal val PipelineContext<Unit, ApplicationCall>.paramId: Long
    get() = call.parameters["id"]?.toLong()
        ?: throw Exception("Param id not found")

internal suspend inline fun <reified T : Any> Either<BaseError, T>.respond(call: ApplicationCall) =
    this.onRight { call.respond(it) }
        .onLeft {
            call.respond(
                HttpStatusCode.fromValue(it.status),
                ProblemDetails(
                    error = it.error,
                    message = it.message,
                    path = "${call.request.httpMethod.value} ${call.request.uri}",
                    status = it.status
                )
            )
        }