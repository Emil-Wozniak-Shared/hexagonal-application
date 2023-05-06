package pl.ejdev.infrastructure.user.adapter

import io.kotest.core.spec.style.FeatureSpec

abstract class UserUseCaseSpec(body: FeatureSpec.() -> Unit = {}) : FeatureSpec(body) {
    protected companion object {
        const val USERNAME = "username"
        const val FIRSTNAME = "firstname"
        const val LASTNAME = "LASTNAME"
        const val EMAIL = "email@email.com"
    }
}
