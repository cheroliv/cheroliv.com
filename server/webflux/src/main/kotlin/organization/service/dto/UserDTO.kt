package organization.service.dto

import organization.domain.User

/**
 * A DTO representing a user, with only the public attributes.
 */
open class UserDTO(
    var id: Long? = null,
    var login: String? = null,
) {

    constructor(user: User) : this(user.id, user.login)

    override fun toString() = "UserDTO{" +
        "login='" + login + '\'' +
        "}"
}
