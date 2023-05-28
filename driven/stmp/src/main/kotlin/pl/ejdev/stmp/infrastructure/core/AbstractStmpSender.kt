package pl.ejdev.stmp.infrastructure.core

import pl.ejdev.stmp.domain.core.StmpSender
import java.util.*

abstract class AbstractStmpSender : StmpSender {

}

fun mailProps(builder: PropertiesDsl.() -> Unit): Properties = PropertiesDsl().also(builder).build()
class PropertiesDsl {

    private val properties: Properties = Properties()

    fun storeProtocol(value: String = "smtp") = apply {
        properties.setProperty("mail.store.protocol", value)
    }

    fun smtpsHost(value: String = "127.0.0.1") = apply {
        properties.setProperty("mail.smtps.host", value)
    }

    fun smtp(stmpBuilder: SmtpDsl.() -> Unit) = apply {
        SmtpDsl(properties).also(stmpBuilder)
    }

    fun transport(transportBuilder: TransportDsl.() -> Unit) = apply {
        TransportDsl(properties).also(transportBuilder)
    }

    operator fun set(key: String, value: String) = apply {
        properties.setProperty(key, value)
    }

    fun build() = properties
}

class SmtpDsl(private val properties: Properties) {
    fun host(value: String = "127.0.0.1") = apply {
        properties.setProperty("mail.smtp.host", value)
    }

    fun port(value: Int = 3025) = apply {
        properties.setProperty("mail.smtp.port", value.toString())
    }

    fun connectionTimeout(value: Int = 15000) = apply {
        properties.setProperty("mail.smtp.connectiontimeout", "$$value")
    }
    fun timeout(value: Int = 15000) = apply {
        properties.setProperty("mail.smtp.timeout", "$$value")
    }
    fun writetimeout(value: Int = 15000) = apply {
        properties.setProperty("mail.smtp.writetimeout", "$$value")
    }
}

class TransportDsl(private val properties: Properties) {
    fun protocol(value: String = "smtp") = apply {
        properties.setProperty("mail.transport.protocol", value)
    }

    fun protocolRfc822(value: String = "smtp") = apply {
        properties.setProperty("mail.transport.protocol.rfc822", value)
    }
}