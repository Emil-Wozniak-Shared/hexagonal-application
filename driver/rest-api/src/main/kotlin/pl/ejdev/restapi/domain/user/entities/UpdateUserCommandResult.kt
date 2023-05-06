package pl.ejdev.restapi.domain.user.entities

class UpdateUserCommandResult(val firstName: String, val lastName: String, val roles: Set<String>)