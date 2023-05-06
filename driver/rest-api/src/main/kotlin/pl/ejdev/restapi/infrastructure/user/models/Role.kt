package pl.ejdev.restapi.infrastructure.user.models

enum class Role {
    ADMIN,
    USER,
    SYSTEM,
    ANON;
}
fun Set<Role>.toNames(): Set<String> = this.map(Role::name).toSet()
