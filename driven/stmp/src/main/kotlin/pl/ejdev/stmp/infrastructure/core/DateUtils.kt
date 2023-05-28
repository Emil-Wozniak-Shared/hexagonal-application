package pl.ejdev.stmp.infrastructure.core

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

fun LocalDateTime.toDate(): Date = this.toInstant(ZoneOffset.UTC).let(Date::from)