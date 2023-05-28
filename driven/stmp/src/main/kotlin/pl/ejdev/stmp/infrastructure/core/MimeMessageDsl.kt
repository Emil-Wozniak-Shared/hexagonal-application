package pl.ejdev.stmp.infrastructure.core

import jakarta.mail.Message
import jakarta.mail.Session
import jakarta.mail.internet.MimeMessage
import java.time.LocalDateTime

internal fun Session.toMimeMessage(builder: MimeMessageDsl.() -> Unit): MimeMessage =
    MimeMessage(this).let(::MimeMessageDsl).also(builder).build()

data class MimeMessageParams(
    val from: String,
    val to: String,
    val subject: String,
    val msg: String
)

internal class MimeMessageDsl(private val mimeMessage: MimeMessage) {
    fun subject(value: String) = apply {
        mimeMessage.subject = value
    }

    fun sendDate(value: LocalDateTime = LocalDateTime.now()) = apply {
        mimeMessage.sentDate = value.toDate()
    }

    fun from(value: String) = apply {
        mimeMessage.setFrom(value)
    }

    fun recipientsTo(value: String) = apply {
        mimeMessage.setRecipients(Message.RecipientType.TO, value)
    }

    fun text(value: String) = apply {
        mimeMessage.setText(value)
    }

    fun build(): MimeMessage = mimeMessage
}