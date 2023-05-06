package pl.ejdev.infrastructure.user.adapter

import arrow.core.raise.either
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import pl.ejdev.error.MustBeNonNullError
import pl.ejdev.infrastructure.UseCaseSpec
import pl.ejdev.infrastructure.expectFailure
import pl.ejdev.infrastructure.expectSuccess
import pl.ejdev.postgres.domain.user.UserRepository
import pl.ejdev.postgres.domain.user.port.out.CreateUserResult
import pl.ejdev.postgres.infrastructure.user.sources.repository.Status
import pl.ejdev.restapi.domain.user.entities.CreateUserCommand

class CreateUserUseCaseAdapterSpec : UserUseCaseSpec() {
    init {
        feature("Create user use case") {
            val repository = mockk<UserRepository>()
            val target = CreateUserUseCaseAdapter(repository)

            scenario("returns user from repository") {
                // given
                coEvery { repository.create(any()) } returns either { createUser() }
                val action = CreateUserCommand(USERNAME, FIRSTNAME, LASTNAME, EMAIL, setOf())
                // when
                target.handle(action)
                    // then
                    .expectSuccess {
                        id shouldBe 1
                        username shouldBe USERNAME
                        firstName shouldBe FIRSTNAME
                        lastName shouldBe LASTNAME
                        email shouldBe EMAIL
                        status shouldBe Status.ACTIVE.name
                        roles.size shouldBe 0
                    }
            }

            scenario("returns error from repository") {
                // given
                coEvery { repository.create(any()) } returns either { raise(MustBeNonNullError("Not found", "")) }
                val action = CreateUserCommand(USERNAME, FIRSTNAME, LASTNAME, EMAIL, setOf())
                // when
                target.handle(action)
                    // then
                    .expectFailure {
                        error shouldBe "Not found"
                        message shouldBe ""
                    }
            }
        }
    }

    private fun createUser(): CreateUserResult =
        CreateUserResult(
            id = 1,
            username = USERNAME,
            firstName = FIRSTNAME,
            lastName = LASTNAME,
            email = EMAIL,
            status = Status.ACTIVE.name,
            roles = setOf()
        )
}

