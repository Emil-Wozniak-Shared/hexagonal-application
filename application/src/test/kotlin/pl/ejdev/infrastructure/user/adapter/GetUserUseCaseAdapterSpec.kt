package pl.ejdev.infrastructure.user.adapter

import arrow.core.raise.either
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import pl.ejdev.error.MustBeNonNullError
import pl.ejdev.infrastructure.expectFailure
import pl.ejdev.infrastructure.expectSuccess
import pl.ejdev.postgres.domain.user.UserRepository
import pl.ejdev.postgres.domain.user.port.out.GetUserResult
import pl.ejdev.postgres.infrastructure.user.sources.repository.Status
import pl.ejdev.restapi.domain.user.entities.GetAllUserQuery
import pl.ejdev.restapi.domain.user.entities.GetUserQuery

class GetUserUseCaseAdapterSpec : UserUseCaseSpec() {
    init {
        feature("Get user use case") {
            val repository = mockk<UserRepository>()
            val target = GetUserUseCaseAdapter(repository)

            scenario("returns user from repository") {
                // given
                coEvery { repository.get(any()) } returns either { createUser() }
                //when
                target.handle(GetUserQuery(1))
                    //then
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
                coEvery { repository.get(any()) } returns either { raise(MustBeNonNullError("Not found", "")) }
                // when
                target.handle(GetUserQuery(1))
                    // then
                    .expectFailure {
                        error shouldBe "Not found"
                        message shouldBe ""
                    }
            }
        }
    }

    private fun createUser(): GetUserResult =
        GetUserResult(
            id = 1,
            username = USERNAME,
            firstName = FIRSTNAME,
            lastName = LASTNAME,
            email = EMAIL,
            status = Status.ACTIVE.name,
            roles = setOf()
        )
}
