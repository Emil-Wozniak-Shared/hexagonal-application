package pl.ejdev.restapi.domain.core.usecases

import arrow.core.Either
import pl.ejdev.error.BaseError

interface UseCase<ACTION : Any, RESULT : Any> {
    suspend fun handle(action: ACTION): Either<BaseError, RESULT>
}