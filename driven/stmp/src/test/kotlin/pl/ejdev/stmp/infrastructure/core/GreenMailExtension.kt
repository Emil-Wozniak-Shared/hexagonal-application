package pl.ejdev.stmp.infrastructure.core

import com.icegreen.greenmail.configuration.GreenMailConfiguration
import com.icegreen.greenmail.configuration.UserBean
import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import com.icegreen.greenmail.util.ServerSetupTest
import io.kotest.core.listeners.AfterEachListener
import io.kotest.core.listeners.BeforeEachListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import jakarta.mail.internet.MimeMessage

object Server {
    fun receives(action: (Array<MimeMessage>) -> Unit) {
        GreenMailExtension.greenMail
            ?.receivedMessages?.let(action)
            ?: greenMailNotStated()
    }

}

private fun greenMailNotStated(): Nothing {
    throw IllegalStateException("GreenMail has not started! Please install GreenMailExtension")
}

internal class GreenMailExtension(
    vararg users: UserBean = arrayOf()
) : BeforeEachListener, AfterEachListener {
    companion object {
        private val config: GreenMailConfiguration = GreenMailConfiguration.aConfig()
        var greenMail: GreenMail? = null

        fun getSmtpSetup(): ServerSetup = greenMail?.smtp?.serverSetup
            ?: greenMailNotStated()

        operator fun invoke(action: Array<UserBean>.() -> Unit): GreenMailExtension =
            arrayOf<UserBean>()
                .also(action)
                .let(::GreenMailExtension)
    }

    init {
        users.forEach {
            config.withUser(it.email, it.login, it.password)
        }
    }

    override suspend fun beforeEach(testCase: TestCase) {
        super.beforeEach(testCase)
        greenMail = GreenMail(ServerSetupTest.SMTP)
        requireNotNull(greenMail)
            .withConfiguration(config)
            .start()
    }

    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        super.afterEach(testCase, result)
        greenMail?.stop() ?: greenMailNotStated()
    }
}

internal fun Array<UserBean>.user(email: String, login: String, password: String) =
    this.plus(UserBean(email, login, password))