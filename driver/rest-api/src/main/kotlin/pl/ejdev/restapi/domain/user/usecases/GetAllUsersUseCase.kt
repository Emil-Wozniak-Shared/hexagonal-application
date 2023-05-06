package pl.ejdev.restapi.domain.user.usecases

import arrow.core.Either
import pl.ejdev.restapi.domain.core.usecases.UseCase
import pl.ejdev.error.BaseError
import pl.ejdev.restapi.domain.user.entities.GetAllUserQuery
import pl.ejdev.restapi.domain.user.entities.GetUserQuery
import pl.ejdev.restapi.domain.user.entities.GetUserQueryResult

interface GetAllUsersUseCase : UseCase<GetAllUserQuery, List<GetUserQueryResult>> {
    override suspend fun handle(action: GetAllUserQuery): Either<BaseError, List<GetUserQueryResult>>
}