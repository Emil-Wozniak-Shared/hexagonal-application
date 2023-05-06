package pl.ejdev.restapi.infrastructure.user

import arrow.core.raise.either
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.ContentType.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.routing.*
import io.mockk.coEvery
import io.mockk.mockk
import org.koin.core.module.Module
import org.koin.dsl.module
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

object UsersRouteSpec : RouteSpec() {
    private val getUserUseCase = mockk<GetUserUseCase>()
    private val getAllUsersUseCase = mockk<GetAllUsersUseCase>()
    private val createUserUseCase = mockk<CreateUserUseCase>()

    override val route: Route.() -> Unit = { users() }
    override val module: Module = module {
        single { getUserUseCase }
        single { getAllUsersUseCase }
        single { createUserUseCase }
    }

    init {
        val getUserQueryResult = GetUserQueryResult(1, USERNAME, FIRST_NAME, LAST_NAME, EMAIL, STATUS, ROLES)
        val createUserQueryResult = CreateUserQueryResult(1, USERNAME, FIRST_NAME, LAST_NAME, EMAIL, STATUS, ROLES)
        feature("User routes") {
            scenario("get users") {
                coEvery { getAllUsersUseCase.handle(any()) } returns either { listOf(getUserQueryResult) }
                request {
                    get("/api/users") { headers { accept(Application.Json) } }.run {
                        status shouldBe OK
                        response<List<GetUserQueryResult>> {
                            get(0).run {
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
            scenario("get user by id") {
                coEvery { getUserUseCase.handle(any()) } returns either { getUserQueryResult }
                request {
                    get("/api/users/1") { headers { accept(Application.Json) } }.run {
                        status shouldBe OK
                        response<GetUserQueryResult> {
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
            scenario("create user") {
                coEvery { createUserUseCase.handle(any()) } returns either { createUserQueryResult }
                request {
                    post("/api/users") {
                        headers { contentType(Application.Json) }
                        setBody(
                            """ {
                                    "username": "john.snow",
                                    "firstName": "John",
                                    "lastName": "Snow",
                                    "email": "john.snow@westeron.com",
                                    "roles": ["USER", "ANON"]
                                 }""".trimIndent()
                        )
                    }.run {
                        status shouldBe OK
                        response<CreateUserQueryResult> {
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
    }
}