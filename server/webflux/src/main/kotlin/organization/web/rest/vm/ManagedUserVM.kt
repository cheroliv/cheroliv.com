package organization.web.rest.vm

import organization.service.dto.AdminUserDTO
import javax.validation.constraints.Size

/**
 * View Model extending the [AdminUserDTO], which is meant to be used in the user management UI.
 */
class ManagedUserVM : AdminUserDTO() {

    @field:Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    var password: String? = null

    override fun toString() = "ManagedUserVM{${super.toString()}}"

    companion object {
        const val PASSWORD_MIN_LENGTH: Int = 4
        const val PASSWORD_MAX_LENGTH: Int = 100
    }
}
