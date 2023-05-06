package pl.ejdev.infrastructure

import arrow.core.Either
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import pl.ejdev.error.BaseError

abstract class UseCaseSpec(body: FeatureSpec.() -> Unit = {}) : FeatureSpec(body)

inline fun <T> Either<BaseError, T>.expectSuccess(assertions: T.() -> Unit) {
    this.isRight() shouldBe true
    this.getOrNull().let(::requireNotNull)
        .run { assertions(this) }
}

inline fun <T> Either<BaseError, T>.expectFailure(assertions: BaseError.() -> Unit) {
    this.isLeft() shouldBe true
    this.leftOrNull().let(::requireNotNull)
        .run { assertions(this) }
}
