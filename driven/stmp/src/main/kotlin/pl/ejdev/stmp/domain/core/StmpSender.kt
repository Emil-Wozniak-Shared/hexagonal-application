package pl.ejdev.stmp.domain.core

import arrow.core.Either
import jakarta.mail.internet.MimeMessage
import pl.ejdev.error.BaseError

interface StmpSender {

    suspend fun send(from: String, to: String, subject: String, msg: String): Either<BaseError, Unit>

}

