package organization.service.mapper

import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import organization.domain.User
import organization.service.dto.AdminUserDTO
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private const val DEFAULT_LOGIN = "johndoe"
private const val DEFAULT_ID = 1L

/**
 * Unit tests for [UserMapper].
 */
class UserMapperTest {

    private lateinit var userMapper: UserMapper

    private lateinit var user: User
    private lateinit var userDto: AdminUserDTO

    @BeforeEach
    fun init() {
        userMapper = UserMapper()
        user = User(
            login = DEFAULT_LOGIN,
            password = RandomStringUtils.random(60),
            activated = true,
            email = "johndoe@localhost",
            firstName = "john",
            lastName = "doe",
            imageUrl = "image_url",
            langKey = "en"
        )

        userDto = AdminUserDTO(user)
    }

    @Test
    fun usersToUserDTOsShouldMapOnlyNonNullUsers() {
        val users = listOf(user, null)

        val userDTOS = userMapper.usersToUserDTOs(users)

        assertThat(userDTOS).isNotEmpty.size().isEqualTo(1)
    }

    @Test
    fun userDTOsToUsersShouldMapOnlyNonNullUsers() {
        val usersDto = mutableListOf(userDto, null)

        val users = userMapper.userDTOsToUsers(usersDto)

        assertThat(users).isNotEmpty.size().isEqualTo(1)
    }

    @Test
    fun userDTOsToUsersWithAuthoritiesStringShouldMapToUsersWithAuthoritiesDomain() {
        userDto.authorities = mutableSetOf("ADMIN")

        val usersDto = listOf(userDto)

        val users = userMapper.userDTOsToUsers(usersDto)

        assertThat(users).isNotEmpty.size().isEqualTo(1)
        assertThat(users[0].authorities).isNotNull
        assertThat(users[0].authorities).isNotEmpty
        assertThat(users[0].authorities.first().name).isEqualTo("ADMIN")
    }

    @Test
    fun userDTOsToUsersMapWithNullAuthoritiesStringShouldReturnUserWithEmptyAuthorities() {
        userDto.authorities = mutableSetOf<String>()

        val usersDto = listOf(userDto)

        val users = userMapper.userDTOsToUsers(usersDto)

        assertThat(users).isNotEmpty.size().isEqualTo(1)
        assertThat(users[0].authorities).isNotNull
        assertThat(users[0].authorities).isEmpty()
    }

    @Test
    fun userDTOToUserMapWithAuthoritiesStringShouldReturnUserWithAuthorities() {
        userDto.authorities = mutableSetOf("ADMIN")

        val user = userMapper.userDTOToUser(userDto)

        assertNotNull(user)
        assertThat(user.authorities).isNotNull
        assertThat(user.authorities).isNotEmpty
        assertThat(user.authorities.first().name).isEqualTo("ADMIN")
    }

    @Test
    fun userDTOToUserMapWithNullAuthoritiesStringShouldReturnUserWithEmptyAuthorities() {
        userDto.authorities = mutableSetOf<String>()

        val user = userMapper.userDTOToUser(userDto)

        assertNotNull(user)
        assertThat(user.authorities).isNotNull
        assertThat(user.authorities).isEmpty()
    }

    @Test
    fun userDTOToUserMapWithNullUserShouldReturnNull() {
        assertNull(userMapper.userDTOToUser(null))
    }

    @Test
    fun testUserFromId() {
        assertThat(userMapper.userFromId(DEFAULT_ID)?.id).isEqualTo(DEFAULT_ID)
        assertNull(userMapper.userFromId(null))
    }
}
