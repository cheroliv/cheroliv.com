package organization.web.rest

import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient
import organization.IntegrationTest
import organization.IntegrationTest.Companion.DEFAULT_TIMEOUT
import organization.config.SYSTEM_ACCOUNT
import organization.domain.Authority
import organization.domain.User
import organization.repository.AuthorityRepository
import organization.repository.EntityManager
import organization.repository.UserRepository
import organization.security.ADMIN
import organization.security.USER
import organization.service.dto.AdminUserDTO
import organization.service.mapper.UserMapper
import organization.web.rest.vm.ManagedUserVM
import java.time.Instant
import kotlin.test.assertNotNull

/**
 * Integration tests for the [UserResource] REST controller.
 */
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_TIMEOUT)
@WithMockUser(authorities = [ADMIN])
@IntegrationTest
class UserResourceIT {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var authorityRepository: AuthorityRepository

    @Autowired
    private lateinit var userMapper: UserMapper

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var webTestClient: WebTestClient

    private lateinit var user: User

    @BeforeEach
    fun initTest() {
        user = initTestUser(userRepository, em)
    }

    @Test
    @Throws(Exception::class)
    fun createUser() {
        val databaseSizeBeforeCreate = userRepository.findAll()
            .collectList().block()!!.size

        // Create the User
        val managedUserVM = ManagedUserVM().apply {
            login = DEFAULT_LOGIN
            password = DEFAULT_PASSWORD
            firstName = DEFAULT_FIRSTNAME
            lastName = DEFAULT_LASTNAME
            email = DEFAULT_EMAIL
            activated = true
            imageUrl = DEFAULT_IMAGEURL
            langKey = DEFAULT_LANGKEY
            authorities = mutableSetOf(USER)
        }

        webTestClient.post().uri("/api/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(managedUserVM))
            .exchange()
            .expectStatus().isCreated

        assertPersistedUsers { userList ->
            // Validate the User in the database
            assertThat(userList).hasSize(databaseSizeBeforeCreate + 1)
            val testUser = userList.first { it.login == DEFAULT_LOGIN }
            assertThat(testUser.login).isEqualTo(DEFAULT_LOGIN)
            assertThat(testUser.firstName).isEqualTo(DEFAULT_FIRSTNAME)
            assertThat(testUser.lastName).isEqualTo(DEFAULT_LASTNAME)
            assertThat(testUser.email).isEqualTo(DEFAULT_EMAIL)
            assertThat(testUser.imageUrl).isEqualTo(DEFAULT_IMAGEURL)
            assertThat(testUser.langKey).isEqualTo(DEFAULT_LANGKEY)
        }
    }

    @Test
    @Throws(Exception::class)
    fun createUserWithExistingId() {
        val databaseSizeBeforeCreate = userRepository.findAll()
            .collectList().block()!!.size

        val managedUserVM = ManagedUserVM().apply {
            id = DEFAULT_ID
            login = DEFAULT_LOGIN
            password = DEFAULT_PASSWORD
            firstName = DEFAULT_FIRSTNAME
            lastName = DEFAULT_LASTNAME
            email = DEFAULT_EMAIL
            activated = true
            imageUrl = DEFAULT_IMAGEURL
            langKey = DEFAULT_LANGKEY
            authorities = mutableSetOf(USER)
        }

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient.post().uri("/api/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(managedUserVM))
            .exchange()
            .expectStatus().isBadRequest

        assertPersistedUsers { userList ->
            // Validate the User in the database
            assertThat(userList).hasSize(databaseSizeBeforeCreate)
        }
    }

    @Test
    @Throws(Exception::class)
    fun createUserWithExistingLogin() {
        // Initialize the database
        userRepository.save(user).block()
        val databaseSizeBeforeCreate = userRepository.findAll()
            .collectList().block()!!.size

        val managedUserVM = ManagedUserVM().apply {
            login = DEFAULT_LOGIN // this login should already be used
            password = DEFAULT_PASSWORD
            firstName = DEFAULT_FIRSTNAME
            lastName = DEFAULT_LASTNAME
            email = "anothermail@localhost"
            activated = true
            imageUrl = DEFAULT_IMAGEURL
            langKey = DEFAULT_LANGKEY
            authorities = mutableSetOf(USER)
        }

        // Create the User
        webTestClient.post().uri("/api/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(managedUserVM))
            .exchange()
            .expectStatus().isBadRequest

        assertPersistedUsers { userList -> assertThat(userList).hasSize(databaseSizeBeforeCreate) }
    }

    @Test
    @Throws(Exception::class)
    fun createUserWithExistingEmail() {
        // Initialize the database
        userRepository.save(user).block()
        val databaseSizeBeforeCreate = userRepository.findAll()
            .collectList().block()!!.size

        val managedUserVM = ManagedUserVM().apply {
            login = "anotherlogin"
            password = DEFAULT_PASSWORD
            firstName = DEFAULT_FIRSTNAME
            lastName = DEFAULT_LASTNAME
            email = DEFAULT_EMAIL // this email should already be used
            activated = true
            imageUrl = DEFAULT_IMAGEURL
            langKey = DEFAULT_LANGKEY
            authorities = mutableSetOf(USER)
        }

        // Create the User
        webTestClient.post().uri("/api/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(managedUserVM))
            .exchange()
            .expectStatus().isBadRequest

        assertPersistedUsers { userList -> assertThat(userList).hasSize(databaseSizeBeforeCreate) }
    }

    @Test
    fun getAllUsers() {
        // Initialize the database
        userRepository.save(user).block()
        authorityRepository
            .findById(USER)
            .flatMap { authority -> authority.name?.let { user.id?.let { it1 -> userRepository.saveUserAuthority(it1, it) } } }
            .block()
        // Get all the users
        val foundUser = webTestClient.get().uri("/api/admin/users?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .returnResult(AdminUserDTO::class.java).responseBody.blockFirst()

        assertNotNull(foundUser)
        assertThat(foundUser.login).isEqualTo(DEFAULT_LOGIN)
        assertThat(foundUser.firstName).isEqualTo(DEFAULT_FIRSTNAME)
        assertThat(foundUser.lastName).isEqualTo(DEFAULT_LASTNAME)
        assertThat(foundUser.email).isEqualTo(DEFAULT_EMAIL)
        assertThat(foundUser.imageUrl).isEqualTo(DEFAULT_IMAGEURL)
        assertThat(foundUser.langKey).isEqualTo(DEFAULT_LANGKEY)
        assertThat(foundUser.authorities).containsExactly(USER)
    }

    @Test
    fun getUser() {
        // Initialize the database
        userRepository.save(user).block()
        authorityRepository
            .findById(USER)
            .flatMap { authority -> authority.name?.let { user.id?.let { it1 -> userRepository.saveUserAuthority(it1, it) } } }
            .block()

        // Get the user
        webTestClient.get().uri("/api/admin/users/{login}", user.login)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("\$.login").isEqualTo(user.login)
            .jsonPath("\$.firstName").isEqualTo(DEFAULT_FIRSTNAME)
            .jsonPath("\$.lastName").isEqualTo(DEFAULT_LASTNAME)
            .jsonPath("\$.email").isEqualTo(DEFAULT_EMAIL)
            .jsonPath("\$.imageUrl").isEqualTo(DEFAULT_IMAGEURL)
            .jsonPath("\$.langKey").isEqualTo(DEFAULT_LANGKEY)
            .jsonPath("\$.authorities").isEqualTo(USER)
    }

    @Test
    fun getNonExistingUser() {
        webTestClient.get().uri("/api/admin/users/unknown")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    @Throws(Exception::class)
    fun updateUser() {
        // Initialize the database
        userRepository.save(user).block()
        val databaseSizeBeforeUpdate = userRepository.findAll()
            .collectList().block()!!.size

        // Update the user
        val updatedUser = userRepository.findById(user.id!!).block()
        assertNotNull(updatedUser)

        val managedUserVM = ManagedUserVM().apply {
            id = updatedUser.id
            login = updatedUser.login
            password = UPDATED_PASSWORD
            firstName = UPDATED_FIRSTNAME
            lastName = UPDATED_LASTNAME
            email = UPDATED_EMAIL
            activated = updatedUser.activated
            imageUrl = UPDATED_IMAGEURL
            langKey = UPDATED_LANGKEY
            createdBy = updatedUser.createdBy
            createdDate = updatedUser.createdDate
            lastModifiedBy = updatedUser.lastModifiedBy
            lastModifiedDate = updatedUser.lastModifiedDate
            authorities = mutableSetOf(USER)
        }

        webTestClient.put().uri("/api/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(managedUserVM))
            .exchange()
            .expectStatus().isOk

        assertPersistedUsers { userList ->
            assertThat(userList).hasSize(databaseSizeBeforeUpdate)
            val testUser = userList.first { it.id == updatedUser.id }
            assertThat(testUser.firstName).isEqualTo(UPDATED_FIRSTNAME)
            assertThat(testUser.lastName).isEqualTo(UPDATED_LASTNAME)
            assertThat(testUser.email).isEqualTo(UPDATED_EMAIL)
            assertThat(testUser.imageUrl).isEqualTo(UPDATED_IMAGEURL)
            assertThat(testUser.langKey).isEqualTo(UPDATED_LANGKEY)
        }
    }

    @Test
    @Throws(Exception::class)
    fun updateUserLogin() {
        // Initialize the database
        userRepository.save(user).block()
        val databaseSizeBeforeUpdate = userRepository.findAll()
            .collectList().block()!!.size

        // Update the user
        val updatedUser = userRepository.findById(user.id!!).block()
        assertNotNull(updatedUser)

        val managedUserVM = ManagedUserVM().apply {
            id = updatedUser.id
            login = UPDATED_LOGIN
            password = UPDATED_PASSWORD
            firstName = UPDATED_FIRSTNAME
            lastName = UPDATED_LASTNAME
            email = UPDATED_EMAIL
            activated = updatedUser.activated
            imageUrl = UPDATED_IMAGEURL
            langKey = UPDATED_LANGKEY
            createdBy = updatedUser.createdBy
            createdDate = updatedUser.createdDate
            lastModifiedBy = updatedUser.lastModifiedBy
            lastModifiedDate = updatedUser.lastModifiedDate
            authorities = mutableSetOf(USER)
        }

        webTestClient.put().uri("/api/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(managedUserVM))
            .exchange()
            .expectStatus().isOk

        assertPersistedUsers { userList ->
            assertThat(userList).hasSize(databaseSizeBeforeUpdate)
            val testUser = userList.first { it.id == updatedUser.id }
            assertThat(testUser.login).isEqualTo(UPDATED_LOGIN)
            assertThat(testUser.firstName).isEqualTo(UPDATED_FIRSTNAME)
            assertThat(testUser.lastName).isEqualTo(UPDATED_LASTNAME)
            assertThat(testUser.email).isEqualTo(UPDATED_EMAIL)
            assertThat(testUser.imageUrl).isEqualTo(UPDATED_IMAGEURL)
            assertThat(testUser.langKey).isEqualTo(UPDATED_LANGKEY)
        }
    }

    @Test
    @Throws(Exception::class)
    fun updateUserExistingEmail() {
        // Initialize the database with 2 users
        userRepository.save(user).block()

        val anotherUser = User(
            login = "jhipster",
            password = RandomStringUtils.random(60),
            activated = true,
            email = "jhipster@localhost",
            firstName = "java",
            lastName = "hipster",
            imageUrl = "",
            createdBy = SYSTEM_ACCOUNT,
            langKey = "en"
        )
        userRepository.save(anotherUser).block()

        // Update the user
        val updatedUser = userRepository.findById(user.id!!).block()
        assertNotNull(updatedUser)

        val managedUserVM = ManagedUserVM().apply {
            id = updatedUser.id
            login = updatedUser.login
            password = updatedUser.password
            firstName = updatedUser.firstName
            lastName = updatedUser.lastName
            email = "jhipster@localhost" // this email should already be used by anotherUser
            activated = updatedUser.activated
            imageUrl = updatedUser.imageUrl
            langKey = updatedUser.langKey
            createdBy = updatedUser.createdBy
            createdDate = updatedUser.createdDate
            lastModifiedBy = updatedUser.lastModifiedBy
            lastModifiedDate = updatedUser.lastModifiedDate
            authorities = mutableSetOf(USER)
        }

        webTestClient.put().uri("/api/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(managedUserVM))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    @Throws(Exception::class)
    fun updateUserExistingLogin() {
        // Initialize the database
        userRepository.save(user).block()

        val anotherUser = User(
            login = "jhipster",
            password = RandomStringUtils.random(60),
            activated = true,
            email = "jhipster@localhost",
            firstName = "java",
            lastName = "hipster",
            imageUrl = "",
            createdBy = SYSTEM_ACCOUNT,
            langKey = "en"
        )
        userRepository.save(anotherUser).block()

        // Update the user
        val updatedUser = userRepository.findById(user.id!!).block()
        assertNotNull(updatedUser)

        val managedUserVM = ManagedUserVM().apply {
            id = updatedUser.id
            login = "jhipster" // this login should already be used by anotherUser
            password = updatedUser.password
            firstName = updatedUser.firstName
            lastName = updatedUser.lastName
            email = updatedUser.email
            activated = updatedUser.activated
            imageUrl = updatedUser.imageUrl
            langKey = updatedUser.langKey
            createdBy = updatedUser.createdBy
            createdDate = updatedUser.createdDate
            lastModifiedBy = updatedUser.lastModifiedBy
            lastModifiedDate = updatedUser.lastModifiedDate
            authorities = mutableSetOf(USER)
        }

        webTestClient.put().uri("/api/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(managedUserVM))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun deleteUser() {
        // Initialize the database
        userRepository.save(user).block()
        val databaseSizeBeforeDelete = userRepository.findAll()
            .collectList().block()!!.size

        // Delete the user
        webTestClient.delete().uri("/api/admin/users/{login}", user.login)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent

        assertPersistedUsers { userList -> assertThat(userList).hasSize(databaseSizeBeforeDelete - 1) }
    }

    @Test
    @Throws(Exception::class)
    fun testUserEquals() {
        equalsVerifier(User::class)
        val user1 = User(id = DEFAULT_ID)
        val user2 = User(id = user1.id)
        assertThat(user1).isEqualTo(user2)
        user2.id = 2L
        assertThat(user1).isNotEqualTo(user2)
        user1.id = null
        assertThat(user1).isNotEqualTo(user2)
    }

    @Test
    fun testUserDTOtoUser() {
        val userDTO = AdminUserDTO(
            id = DEFAULT_ID,
            login = DEFAULT_LOGIN,
            firstName = DEFAULT_FIRSTNAME,
            lastName = DEFAULT_LASTNAME,
            email = DEFAULT_EMAIL,
            activated = true,
            imageUrl = DEFAULT_IMAGEURL,
            langKey = DEFAULT_LANGKEY,
            createdBy = DEFAULT_LOGIN,
            lastModifiedBy = DEFAULT_LOGIN,
            authorities = mutableSetOf(USER)
        )

        val user = userMapper.userDTOToUser(userDTO)
        assertNotNull(user)
        assertThat(user.id).isEqualTo(DEFAULT_ID)
        assertThat(user.login).isEqualTo(DEFAULT_LOGIN)
        assertThat(user.firstName).isEqualTo(DEFAULT_FIRSTNAME)
        assertThat(user.lastName).isEqualTo(DEFAULT_LASTNAME)
        assertThat(user.email).isEqualTo(DEFAULT_EMAIL)
        assertThat(user.activated).isTrue
        assertThat(user.imageUrl).isEqualTo(DEFAULT_IMAGEURL)
        assertThat(user.langKey).isEqualTo(DEFAULT_LANGKEY)
        assertThat(user.createdBy).isNull()
        assertThat(user.createdDate).isNotNull()
        assertThat(user.lastModifiedBy).isNull()
        assertThat(user.lastModifiedDate).isNotNull()
        assertThat(user.authorities).extracting("name").containsExactly(USER)
    }

    @Test
    fun testUserToUserDTO() {
        user.id = DEFAULT_ID
        user.createdBy = DEFAULT_LOGIN
        user.createdDate = Instant.now()
        user.lastModifiedBy = DEFAULT_LOGIN
        user.lastModifiedDate = Instant.now()
        user.authorities = mutableSetOf(Authority(name = USER))

        val userDTO = userMapper.userToAdminUserDTO(user)

        assertThat(userDTO.id).isEqualTo(DEFAULT_ID)
        assertThat(userDTO.login).isEqualTo(DEFAULT_LOGIN)
        assertThat(userDTO.firstName).isEqualTo(DEFAULT_FIRSTNAME)
        assertThat(userDTO.lastName).isEqualTo(DEFAULT_LASTNAME)
        assertThat(userDTO.email).isEqualTo(DEFAULT_EMAIL)
        assertThat(userDTO.activated).isTrue
        assertThat(userDTO.imageUrl).isEqualTo(DEFAULT_IMAGEURL)
        assertThat(userDTO.langKey).isEqualTo(DEFAULT_LANGKEY)
        assertThat(userDTO.createdBy).isEqualTo(DEFAULT_LOGIN)
        assertThat(userDTO.createdDate).isEqualTo(user.createdDate)
        assertThat(userDTO.lastModifiedBy).isEqualTo(DEFAULT_LOGIN)
        assertThat(userDTO.lastModifiedDate).isEqualTo(user.lastModifiedDate)
        assertThat(userDTO.authorities).containsExactly(USER)
        assertThat(userDTO.toString()).isNotNull()
    }

    @Test
    fun testAuthorityEquals() {
        val authorityA = Authority()
        assertThat(authorityA)
            .isNotEqualTo(null)
            .isNotEqualTo(Any())
        assertThat(authorityA.hashCode()).isEqualTo(31)
        assertThat(authorityA.toString()).isNotNull()

        val authorityB = Authority()
        assertThat(authorityA.name).isEqualTo(authorityB.name)

        authorityB.name = ADMIN
        assertThat(authorityA).isNotEqualTo(authorityB)

        authorityA.name = USER
        assertThat(authorityA).isNotEqualTo(authorityB)

        authorityB.name = USER
        assertThat(authorityA)
            .isEqualTo(authorityB)
            .hasSameHashCodeAs(authorityB)
    }

    companion object {

        private const val DEFAULT_LOGIN = "johndoe"
        private const val UPDATED_LOGIN = "jhipster"

        private const val DEFAULT_ID = 1L

        private const val DEFAULT_PASSWORD = "passjohndoe"
        private const val UPDATED_PASSWORD = "passjhipster"

        private const val DEFAULT_EMAIL = "johndoe@localhost"
        private const val UPDATED_EMAIL = "jhipster@localhost"

        private const val DEFAULT_FIRSTNAME = "john"
        private const val UPDATED_FIRSTNAME = "jhipsterFirstName"

        private const val DEFAULT_LASTNAME = "doe"
        private const val UPDATED_LASTNAME = "jhipsterLastName"

        private const val DEFAULT_IMAGEURL = "http://placehold.it/50x50"
        private const val UPDATED_IMAGEURL = "http://placehold.it/40x40"

        private const val DEFAULT_LANGKEY = "en"
        private const val UPDATED_LANGKEY = "fr"

        /**
         * Create a User.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which has a required relationship to the User entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager?): User {
            return User(
                login = DEFAULT_LOGIN + RandomStringUtils.randomAlphabetic(5),
                password = RandomStringUtils.random(60),
                activated = true,
                email = RandomStringUtils.randomAlphabetic(5) + DEFAULT_EMAIL,
                firstName = DEFAULT_FIRSTNAME,
                lastName = DEFAULT_LASTNAME,
                imageUrl = DEFAULT_IMAGEURL,
                createdBy = SYSTEM_ACCOUNT,
                langKey = DEFAULT_LANGKEY
            )
        }

        /**
         * Delete all the users from the database.
         */
        @JvmStatic
        fun deleteEntities(em: EntityManager) {
            try {
                em.deleteAll("jhi_user_authority").block()
                em.deleteAll(User::class.java).block()
            } catch (e: Exception) {
                // It can fail, if other entities are still referring this - it will be removed later.
            }
        }

        /**
         * Setups the database with one user.
         */
        @JvmStatic
        fun initTestUser(userRepository: UserRepository, em: EntityManager): User {
            userRepository.deleteAllUserAuthorities().block()
            userRepository.deleteAll().block()
            val user = createEntity(em)
            user.login = DEFAULT_LOGIN
            user.email = DEFAULT_EMAIL
            return user
        }
    }

    fun assertPersistedUsers(userAssertion: (List<User>) -> Unit) {
        userAssertion(userRepository.findAll().collectList().block())
    }
}
