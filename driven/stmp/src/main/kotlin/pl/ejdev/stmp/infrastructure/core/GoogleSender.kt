package pl.ejdev.stmp.infrastructure.core

import arrow.core.Either
import arrow.core.Either.Companion.catchOrThrow
import arrow.core.raise.Raise
import arrow.core.raise.either
import jakarta.mail.MessagingException
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.AddressException
import jakarta.mail.internet.MimeMessage
import pl.ejdev.error.BaseError
import pl.ejdev.error.EmailError
import java.util.*

private const val INCORRECT_EMAIL_ADDRESS = "Incorrect email address"

private val EMAIL_PATTERN =
    "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])".toRegex()

object GoogleMailSender : AbstractStmpSender() {
    private var mailProps: Properties = Properties()

    /**
     * Send email using GMail SMTP server.
     *
     * @param from GMail username
     * @param to TO recipient
     * @param msg CC recipient. Can be empty if there is no CC recipient
     * @throws AddressException if the email address parse failed
     * @throws MessagingException if the connection is dead or not in the connected state or if the message is not a MimeMessage
     */
    @Throws(AddressException::class, MessagingException::class)
    override suspend fun send(from: String, to: String, subject: String, msg: String): Either<BaseError, Unit> =
        createMessage(MimeMessageParams(from, to, subject, msg))
            .onRight(Transport::send)
            .map { println("Sent message successfully....") }

    private fun createMessage(params: MimeMessageParams): Either<BaseError, MimeMessage> =
        either { createMimeMessage(mailProps.toSession(), params).bind() }

    private fun createMimeMessage(session: Session, params: MimeMessageParams): Either<BaseError, MimeMessage> =
        either {
            val (from, to, subject, msg) = params
            when {
                !from.matches(EMAIL_PATTERN) ->
                    raise(EmailError(INCORRECT_EMAIL_ADDRESS, "'from' address is not an email"))

                !to.matches(EMAIL_PATTERN) ->
                    raise(EmailError(INCORRECT_EMAIL_ADDRESS, "'to' address is not an email"))

                else -> mimeMessage(session, subject, from, to, msg)
            }
        }

    private fun Raise<BaseError>.mimeMessage(
        session: Session,
        subject: String,
        from: String,
        to: String,
        msg: String
    ): MimeMessage = catchOrThrow<Exception, MimeMessage> {
        session.toMimeMessage {
            subject(subject)
            sendDate()
            from(from)
            recipientsTo(to)
            text(msg)
        }
    }
        .mapLeft { EmailError(INCORRECT_EMAIL_ADDRESS, it.message ?: "Email error") }
        .bind()

    /**
     * If set to false, the QUIT command is sent and the connection is immediately closed. If set
     * to true (the default), causes the transport to wait for the response to the QUIT command.
     *
     * ref :   http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
     * http://forum.java.sun.com/thread.jspa?threadID=5205249
     * smtpsend.java - demo program from javamail
     */
    fun withMailProps(
        builder: PropertiesDsl.() -> Unit
    ): GoogleMailSender = apply {
        mailProps(builder).let { mailProps = it }
    }
}