package organization.service.mapper

import org.mapstruct.BeanMapping
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.Named
import org.springframework.stereotype.Service
import organization.domain.Authority
import organization.domain.User
import organization.service.dto.AdminUserDTO
import organization.service.dto.UserDTO

/**
 * Mapper for the entity [User] and its DTO called [UserDTO].
 *
 * Normal mappers are generated using MapStruct, this one is hand-coded as MapStruct
 * support is still in beta, and requires a manual step with an IDE.
 */
@Service
class UserMapper {

    fun usersToUserDTOs(users: List<User?>): MutableList<UserDTO> =
        users.asSequence()
            .filterNotNull()
            .mapTo(mutableListOf()) { userToUserDTO(it) }

    fun userToUserDTO(user: User): UserDTO = UserDTO(user)

    fun usersToAdminUserDTOs(users: List<User>): MutableList<AdminUserDTO> =
        users.asSequence()
            .filterNotNull()
            .mapTo(mutableListOf()) { userToAdminUserDTO(it) }

    fun userToAdminUserDTO(user: User) = AdminUserDTO(user)

    fun userDTOsToUsers(userDTOs: List<AdminUserDTO?>) =
        userDTOs.asSequence()
            .mapNotNullTo(mutableListOf()) { userDTOToUser(it) }

    fun userDTOToUser(userDTO: AdminUserDTO?) =
        when (userDTO) {
            null -> null
            else -> {
                User(
                    id = userDTO.id,
                    login = userDTO.login,
                    firstName = userDTO.firstName,
                    lastName = userDTO.lastName,
                    email = userDTO.email,
                    imageUrl = userDTO.imageUrl,
                    activated = userDTO.activated,
                    langKey = userDTO.langKey,
                    authorities = authoritiesFromStrings(userDTO.authorities)
                )
            }
        }

    private fun authoritiesFromStrings(authoritiesAsString: Set<String>?): MutableSet<Authority> =
        authoritiesAsString?.mapTo(mutableSetOf()) { Authority(name = it) } ?: mutableSetOf()

    fun userFromId(id: Long?) = id?.let { User(id = it) }

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    fun toDtoId(user: User?): UserDTO? {
        if (user == null) {
            return null
        }
        val userDto = UserDTO()
        userDto.id = user.id
        return userDto
    }

    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    fun toDtoIdSet(users: Set<User>?): Set<UserDTO>? {
        if (users == null) {
            return emptySet<UserDTO>()
        }

        val userSet = hashSetOf<UserDTO>()
        users.forEach {
            this.toDtoId(it)?.let {
                userSet.add(it)
            }
        }
        return userSet
    }

    @Named("login")
    @BeanMapping(ignoreByDefault = true)
    @Mappings(
        Mapping(target = "id", source = "id"),
        Mapping(target = "login", source = "login")
    )
    fun toDtoLogin(user: User?): UserDTO? {
        if (user == null) {
            return null
        }
        val userDto = UserDTO()
        userDto.id = user.id
        userDto.login = user.login
        return userDto
    }

    @Named("loginSet")
    @BeanMapping(ignoreByDefault = true)
    @Mappings(
        Mapping(target = "id", source = "id"),
        Mapping(target = "login", source = "login")
    )
    fun toDtoLoginSet(users: Set<User>?): Set<UserDTO>? {
        if (users == null) {
            return emptySet<UserDTO>()
        }

        val userSet = hashSetOf<UserDTO>()
        users.forEach {
            this.toDtoLogin(it)?.let {
                userSet.add(it)
            }
        }

        return userSet
    }
}
