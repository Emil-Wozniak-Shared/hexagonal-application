package pl.ejdev.stmp.infrastructure.core

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class GoogleSenderSpec : FeatureSpec({
    extension(
        GreenMailExtension {
            user(FROM, "emil.wozniak.2020", "1234")
        }
    )
    feature("Send mail") {
        scenario("Gmail mail sender send message") {
            GoogleMailSender
                .withMailProps { mailSetup() }
                .send(
                    from = FROM,
                    to = TO,
                    subject = SUBJECT,
                    msg = BODY
                )
            Server.receives { emails ->
                emails.size shouldBe 1
                with(emails.first()) {
                    subject shouldBe SUBJECT
                    content shouldBe BODY
                }
            }
        }
        scenario("Gmail mail sender throws exception on incorrect email address") {
            GoogleMailSender
                .withMailProps { mailSetup() }
                .send(
                    from = "emil.wozniak.2020",
                    to = TO,
                    subject = SUBJECT,
                    msg = BODY
                )
                .leftOrNull()!!
                .run {
                    error shouldBe "Incorrect email address"
                    message shouldBe "'from' address is not an email"
                }
            Server.receives { emails ->
                emails.size shouldBe 0
            }
        }
    }
})

private const val TO = "emil.wozniak.2020@gmail.com"
private const val FROM = "emil.wozniak.2020@gmail.com"
private const val SUBJECT = "subject"
private const val BODY = "body"

private fun PropertiesDsl.mailSetup() {
    val setup = GreenMailExtension.getSmtpSetup()
    smtp {
        host(setup.bindAddress)
        port(setup.port)
        connectionTimeout()
    }
    transport {
        protocol()
        protocolRfc822()
    }
    storeProtocol()
    smtpsHost()
}
