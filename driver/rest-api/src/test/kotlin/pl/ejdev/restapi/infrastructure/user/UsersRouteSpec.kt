package pl.ejdev.restapi.infrastructure.user

import arrow.core.raise.either
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import pl.ejdev.restapi.domain.user.entities.CreateUserQueryResult
import pl.ejdev.restapi.domain.user.entities.GetUserQueryResult
import pl.ejdev.restapi.domain.user.usecases.CreateUserUseCase
import pl.ejdev.restapi.domain.user.usecases.GetAllUsersUseCase
import pl.ejdev.restapi.domain.user.usecases.GetUserUseCase

private const val USERNAME = "john.snow"
private const val FIRST_NAME = "John"
private const val LAST_NAME = "Snow"
private const val EMAIL = "john.snow@westeron.com"
private const val STATUS = "ACTIVE"
private val ROLES = setOf("USER", "ANON")

private fun Application.module() {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    routing { route("/api") { users() } }
}

suspend inline fun <reified T : Any> HttpResponse.receive(): T {
    val mapper = jacksonObjectMapper()
    return body<String>()
        .let(mapper::readValue)
}

class UsersRouteSpec : FeatureSpec({
    val getUserUseCase = mockk<GetUserUseCase>()
    val getAllUsersUseCase = mockk<GetAllUsersUseCase>()
    val createUserUseCase = mockk<CreateUserUseCase>()
    val getUserQueryResult = GetUserQueryResult(1, USERNAME, FIRST_NAME, LAST_NAME, EMAIL, STATUS, setOf())
    beforeSpec {
        startKoin { modules(module {
            single { getUserUseCase }
            single { getAllUsersUseCase }
            single { createUserUseCase }
        }) }
    }
    feature("User routes") {
        scenario("get users") {
            coEvery { getAllUsersUseCase.handle(any()) } returns either { listOf(getUserQueryResult) }
            testApplication {
                application { module() }
                client.get("/api/users"){
                    headers { accept(ContentType.Application.Json) }
                }.run {
                    status shouldBe OK
                    receive<List<GetUserQueryResult>>().run {
                        get(0).run {
                            id shouldBe 1
                            username shouldBe USERNAME
                            firstName shouldBe FIRST_NAME
                            lastName shouldBe LAST_NAME
                            email shouldBe EMAIL
                            status shouldBe STATUS
                            roles shouldBe setOf()
                        }
                    }
                }
            }
        }
        scenario("get user by id") {
            coEvery { getUserUseCase.handle(any()) } returns either {
                getUserQueryResult
            }
            testApplication {
                application { module() }
                client.get("/api/users/1") {
                    headers { accept(ContentType.Application.Json) }
                }.run {
                    status shouldBe OK
                    receive<GetUserQueryResult>().run {
                        id shouldBe 1
                        username shouldBe USERNAME
                        firstName shouldBe FIRST_NAME
                        lastName shouldBe LAST_NAME
                        email shouldBe EMAIL
                        status shouldBe STATUS
                        roles shouldBe setOf()
                    }
                }
            }
        }
        scenario("create user") {
            coEvery { createUserUseCase.handle(any()) } returns either {
                CreateUserQueryResult(1, USERNAME, FIRST_NAME, LAST_NAME, EMAIL, STATUS, ROLES)
            }
            testApplication {
                application { module() }
                client.post("/api/users") {
                    headers { contentType(ContentType.Application.Json) }
                    setBody(
                        """
                        {
                          "username": "john.snow",
                          "firstName": "John",
                          "lastName": "Snow",
                          "email": "john.snow@westeron.com",
                          "roles": ["USER", "ANON"]
                        }
                    """.trimIndent()
                    )
                }.apply {
                    status shouldBe OK
                    receive<CreateUserQueryResult>().run {
                        id shouldBe 1
                        username shouldBe USERNAME
                        firstName shouldBe FIRST_NAME
                        lastName shouldBe LAST_NAME
                        email shouldBe EMAIL
                        status shouldBe STATUS
                        roles shouldBe ROLES
                    }
                }
            }
        }
    }
}), KoinTest