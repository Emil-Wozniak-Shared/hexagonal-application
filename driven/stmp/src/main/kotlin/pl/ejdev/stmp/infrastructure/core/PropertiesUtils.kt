package pl.ejdev.stmp.infrastructure.core

import jakarta.mail.Authenticator
import jakarta.mail.Session
import java.util.*

fun Properties.toSession( authenticator: Authenticator? = null): Session =
    Session.getInstance(this, authenticator)