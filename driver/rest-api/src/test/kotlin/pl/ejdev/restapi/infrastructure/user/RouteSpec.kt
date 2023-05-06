package pl.ejdev.restapi.infrastructure.user

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.core.spec.style.FeatureSpec
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import org.koin.core.context.startKoin
import org.koin.test.KoinTest

private fun Application.module(module: Route.() -> Unit) {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    routing { route("/api") { module() } }
}

private val mapper = jacksonObjectMapper()
internal suspend inline fun <reified T : Any> HttpResponse.response(check: T.() -> Unit): Unit = body<String>()
    .let { mapper.readValue<T>(it) }
    .let { check(it) }

internal suspend fun request(module: Route.() -> Unit, testCase: suspend HttpClient.() -> Unit) {
    testApplication {
        application { this.module(module) }
        testCase(client)
    }
}

abstract class RouteSpec(body: FeatureSpec.() -> Unit = {}) : FeatureSpec(), KoinTest, RouteSpecRoot {
    init {
        beforeSpec {
            startKoin {
                modules(module)
            }
        }
        body()
    }
}

internal interface RouteSpecRoot {
    val module: org.koin.core.module.Module
}