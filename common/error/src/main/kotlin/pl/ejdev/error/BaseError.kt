package pl.ejdev.error


open class BaseError(
    open val error: String,
    open val message: String,
    val status: Int,
)

class InternalError(exception: Exception) : BaseError(
    error = "${exception.message}",
    message = exception.stackTraceToString(),
    status = 500
)

class MustBeNonNullError(
    override val error: String,
    override val message: String,
) : BaseError(error, message, 404)