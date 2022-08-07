package organization.web.rest

import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import organization.IntegrationTest
import organization.IntegrationTest.Companion.DEFAULT_TIMEOUT
import organization.config.DEFAULT_LANGUAGE
import organization.config.SYSTEM_ACCOUNT
import organization.domain.User
import organization.repository.AuthorityRepository
import organization.repository.UserRepository
import organization.security.ADMIN
import organization.security.USER
import organization.service.UserService
import organization.service.dto.AdminUserDTO
import organization.service.dto.PasswordChangeDTO
import organization.web.rest.vm.KeyAndPasswordVM
import organization.web.rest.vm.ManagedUserVM
import java.time.Instant
import kotlin.test.assertContains

/**
 * Integrations tests for the [AccountResource] REST controller.
 */
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_TIMEOUT)
@WithMockUser(value = TEST_USER_LOGIN)
@IntegrationTest
class AccountResourceIT {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var authorityRepository: AuthorityRepository

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var accountWebTestClient: WebTestClient

    @Test
    @WithUnauthenticatedMockUser
    @Throws(Exception::class)
    fun testNonAuthenticatedUser() {
        accountWebTestClient.get().uri("/api/authenticate")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody().isEmpty
    }

    @Test
    fun testAuthenticatedUser() {
        accountWebTestClient
            .get().uri("/api/authenticate")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<String>().isEqualTo(TEST_USER_LOGIN)
    }

    @Test
    fun testGetExistingAccount() {

        val authorities = mutableSetOf(ADMIN)

        val user = AdminUserDTO(
            login = TEST_USER_LOGIN,
            firstName = "john",
            lastName = "doe",
            email = "john.doe@jhipster.com",
            imageUrl = "http://placehold.it/50x50",
            langKey = "en",
            authorities = authorities
        )
        userService.createUser(user).block()

        accountWebTestClient.get().uri("/api/account")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
            .expectBody()
            .jsonPath("\$.login").isEqualTo(TEST_USER_LOGIN)
            .jsonPath("\$.firstName").isEqualTo("john")
            .jsonPath("\$.lastName").isEqualTo("doe")
            .jsonPath("\$.email").isEqualTo("john.doe@jhipster.com")
            .jsonPath("\$.imageUrl").isEqualTo("http://placehold.it/50x50")
            .jsonPath("\$.langKey").isEqualTo("en")
            .jsonPath("\$.authorities").isEqualTo(ADMIN)
    }

    @Test
    fun testGetUnknownAccount() {
        accountWebTestClient.get().uri("/api/account")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    @Throws(Exception::class)
    fun testRegisterValid() {
        val validUser = ManagedUserVM().apply {
            login = "test-register-valid"
            password = "password"
            firstName = "Alice"
            lastName = "Test"
            email = "test-register-valid@example.com"
            imageUrl = "http://placehold.it/50x50"
            langKey = DEFAULT_LANGUAGE
            authorities = mutableSetOf(USER)
        }
        assertThat(userRepository.findOneByLogin("test-register-valid").blockOptional()).isEmpty

        accountWebTestClient.post().uri("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(validUser))
            .exchange()
            .expectStatus().isCreated

        assertThat(userRepository.findOneByLogin("test-register-valid").blockOptional()).isPresent
    }

    @Test
    @Throws(Exception::class)
    fun testRegisterInvalidLogin() {
        val invalidUser = ManagedUserVM().apply {
            login = "funky-log(n" // <-- invalid
            password = "password"
            firstName = "Funky"
            lastName = "One"
            email = "funky@example.com"
            activated = true
            imageUrl = "http://placehold.it/50x50"
            langKey = DEFAULT_LANGUAGE
            authorities = mutableSetOf(USER)
        }

        accountWebTestClient.post().uri("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(invalidUser))
            .exchange()
            .expectStatus().isBadRequest

        val user = userRepository.findOneByEmailIgnoreCase("funky@example.com").blockOptional()
        assertThat(user).isEmpty
    }

    @Test
    @Throws(Exception::class)
    fun testRegisterInvalidEmail() {
        val invalidUser = ManagedUserVM().apply {
            login = "bob"
            password = "password"
            firstName = "Bob"
            lastName = "Green"
            email = "invalid" // <-- invalid
            activated = true
            imageUrl = "http://placehold.it/50x50"
            langKey = DEFAULT_LANGUAGE
            authorities = mutableSetOf(USER)
        }

        accountWebTestClient.post().uri("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(invalidUser))
            .exchange()
            .expectStatus().isBadRequest

        val user = userRepository.findOneByLogin("bob").blockOptional()
        assertThat(user).isEmpty
    }

    @Test
    @Throws(Exception::class)
    fun testRegisterInvalidPassword() {
        val invalidUser = ManagedUserVM().apply {
            login = "bob"
            password = "123" // password with only 3 digits
            firstName = "Bob"
            lastName = "Green"
            email = "bob@example.com"
            activated = true
            imageUrl = "http://placehold.it/50x50"
            langKey = DEFAULT_LANGUAGE
            authorities = mutableSetOf(USER)
        }

        accountWebTestClient.post().uri("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(invalidUser))
            .exchange()
            .expectStatus().isBadRequest

        val user = userRepository.findOneByLogin("bob").blockOptional()
        assertThat(user).isEmpty
    }

    @Test
    @Throws(Exception::class)
    fun testRegisterNullPassword() {
        val invalidUser = ManagedUserVM().apply {
            login = "bob"
            password = null // invalid null password
            firstName = "Bob"
            lastName = "Green"
            email = "bob@example.com"
            activated = true
            imageUrl = "http://placehold.it/50x50"
            langKey = DEFAULT_LANGUAGE
            authorities = mutableSetOf(USER)
        }

        accountWebTestClient.post().uri("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(invalidUser))
            .exchange()
            .expectStatus().isBadRequest

        val user = userRepository.findOneByLogin("bob").blockOptional()
        assertThat(user).isEmpty
    }

    @Test
    @Throws(Exception::class)
    fun testRegisterDuplicateLogin() {
        // First registration
        val firstUser = ManagedUserVM().apply {
            login = "alice"
            password = "password"
            firstName = "Alice"
            lastName = "Something"
            email = "alice@example.com"
            imageUrl = "http://placehold.it/50x50"
            langKey = DEFAULT_LANGUAGE
            authorities = mutableSetOf(USER)
        }

        // Duplicate login, different email
        val secondUser = ManagedUserVM().apply {
            login = firstUser.login
            password = firstUser.password
            firstName = firstUser.firstName
            lastName = firstUser.lastName
            email = "alice2@example.com"
            imageUrl = firstUser.imageUrl
            langKey = firstUser.langKey
            createdBy = firstUser.createdBy
            createdDate = firstUser.createdDate
            lastModifiedBy = firstUser.lastModifiedBy
            lastModifiedDate = firstUser.lastModifiedDate
            authorities = firstUser.authorities?.toMutableSet()
        }

        // First user
        accountWebTestClient.post().uri("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(firstUser))
            .exchange()
            .expectStatus().isCreated

        // Second (non activated) user
        accountWebTestClient.post().uri("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(secondUser))
            .exchange()
            .expectStatus().isCreated

        val testUser = userRepository.findOneByEmailIgnoreCase("alice2@example.com").blockOptional()
        assertThat(testUser).isPresent
        testUser.get().activated = true
        userRepository.save(testUser.get()).block()

        // Second (already activated) user
        accountWebTestClient.post().uri("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(secondUser))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    @Throws(Exception::class)
    fun testRegisterDuplicateEmail() {
        // First user
        val firstUser = ManagedUserVM().apply {
            login = "test-register-duplicate-email"
            password = "password"
            firstName = "Alice"
            lastName = "Test"
            email = "test-register-duplicate-email@example.com"
            imageUrl = "http://placehold.it/50x50"
            langKey = DEFAULT_LANGUAGE
            authorities = mutableSetOf(USER)
        }

        // Register first user
        accountWebTestClient.post().uri("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(firstUser))
            .exchange()
            .expectStatus().isCreated

        val testUser1 = userRepository.findOneByLogin("test-register-duplicate-email").blockOptional()
        assertThat(testUser1).isPresent

        // Duplicate email, different login
        val secondUser = ManagedUserVM().apply {
            login = "test-register-duplicate-email-2"
            password = firstUser.password
            firstName = firstUser.firstName
            lastName = firstUser.lastName
            email = firstUser.email
            imageUrl = firstUser.imageUrl
            langKey = firstUser.langKey
            authorities = firstUser.authorities?.toMutableSet()
        }

        // Register second (non activated) user
        accountWebTestClient.post().uri("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(secondUser))
            .exchange()
            .expectStatus().isCreated

        val testUser2 = userRepository.findOneByLogin("test-register-duplicate-email").blockOptional()
        assertThat(testUser2).isEmpty

        val testUser3 = userRepository.findOneByLogin("test-register-duplicate-email-2").blockOptional()
        assertThat(testUser3).isPresent

        // Duplicate email - with uppercase email address
        val userWithUpperCaseEmail = ManagedUserVM().apply {
            id = firstUser.id
            login = "test-register-duplicate-email-3"
            password = firstUser.password
            firstName = firstUser.firstName
            lastName = firstUser.lastName
            email = "TEST-register-duplicate-email@example.com"
            imageUrl = firstUser.imageUrl
            langKey = firstUser.langKey
            authorities = firstUser.authorities?.toMutableSet()
        }

        // Register third (not activated) user
        accountWebTestClient.post().uri("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(userWithUpperCaseEmail))
            .exchange()
            .expectStatus().isCreated

        val testUser4 = userRepository.findOneByLogin("test-register-duplicate-email-3").blockOptional()
        assertThat(testUser4).isPresent
        assertThat(testUser4.get().email).isEqualTo("test-register-duplicate-email@example.com")

        testUser4.get().activated = true
        userService.updateUser((AdminUserDTO(testUser4.get()))).block()

        // Register 4th (already activated) user
        accountWebTestClient.post().uri("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(secondUser))
            .exchange()
            .expectStatus().is4xxClientError
    }

    @Test
    @Throws(Exception::class)
    fun testRegisterAdminIsIgnored() {
        val validUser = ManagedUserVM().apply {
            login = "badguy"
            password = "password"
            firstName = "Bad"
            lastName = "Guy"
            email = "badguy@example.com"
            activated = true
            imageUrl = "http://placehold.it/50x50"
            langKey = DEFAULT_LANGUAGE
            authorities = mutableSetOf(ADMIN)
        }

        accountWebTestClient.post().uri("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(validUser))
            .exchange()
            .expectStatus().isCreated

        val userDup = userRepository.findOneWithAuthoritiesByLogin("badguy").blockOptional()
        assertThat(userDup).isPresent
        assertThat(userDup.get().authorities).hasSize(1)
        assertContains(userDup.get().authorities, authorityRepository.findById(USER).block())
    }

    @Test
    fun testActivateAccount() {
        val activationKey = "some activation key"
        var user = User(
            login = "activate-account",
            email = "activate-account@example.com",
            password = RandomStringUtils.random(60),
            activated = false,
            createdBy = SYSTEM_ACCOUNT,
            activationKey = activationKey
        )

        userRepository.save(user).block()

        accountWebTestClient.get().uri("/api/activate?key={activationKey}", activationKey)
            .exchange()
            .expectStatus().isOk

        user = userRepository.findOneByLogin(user.login!!).block()!!
        assertThat(user.activated).isTrue
    }

    @Test

    fun testActivateAccountWithWrongKey() {
        accountWebTestClient.get().uri("/api/activate?key=wrongActivationKey")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    @WithMockUser("save-account")
    @Throws(Exception::class)
    fun testSaveAccount() {
        val user = User(
            login = "save-account",
            email = "save-account@example.com",
            password = RandomStringUtils.random(60),
            createdBy = SYSTEM_ACCOUNT,
            activated = true
        )

        userRepository.save(user).block()

        val userDTO = AdminUserDTO(
            login = "not-used",
            firstName = "firstname",
            lastName = "lastname",
            email = "save-account@example.com",
            activated = false,
            imageUrl = "http://placehold.it/50x50",
            langKey = DEFAULT_LANGUAGE,
            authorities = mutableSetOf(ADMIN)
        )

        accountWebTestClient.post().uri("/api/account")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(userDTO))
            .exchange()
            .expectStatus().isOk

        val updatedUser = userRepository.findOneWithAuthoritiesByLogin(user?.login!!).block()
        assertThat(updatedUser?.firstName).isEqualTo(userDTO.firstName)
        assertThat(updatedUser?.lastName).isEqualTo(userDTO.lastName)
        assertThat(updatedUser?.email).isEqualTo(userDTO.email)
        assertThat(updatedUser?.langKey).isEqualTo(userDTO.langKey)
        assertThat(updatedUser?.password).isEqualTo(user.password)
        assertThat(updatedUser?.imageUrl).isEqualTo(userDTO.imageUrl)
        assertThat(updatedUser?.activated).isTrue
        assertThat(updatedUser?.authorities).isEmpty()
    }

    @Test
    @WithMockUser("save-invalid-email")
    fun testSaveInvalidEmail() {
        val user = User(
            login = "save-invalid-email",
            email = "save-invalid-email@example.com",
            password = RandomStringUtils.random(60),
            createdBy = SYSTEM_ACCOUNT,
            activated = true
        )

        userRepository.save(user).block()

        val userDTO = AdminUserDTO(
            login = "not-used",
            firstName = "firstname",
            lastName = "lastname",
            email = "invalid email",
            activated = false,
            imageUrl = "http://placehold.it/50x50",
            langKey = DEFAULT_LANGUAGE,
            authorities = mutableSetOf(ADMIN)
        )

        accountWebTestClient.post().uri("/api/account")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(userDTO))
            .exchange()
            .expectStatus().isBadRequest

        assertThat(userRepository.findOneByEmailIgnoreCase("invalid email").blockOptional()).isNotPresent
    }

    @Test
    @WithMockUser("save-existing-email")
    fun testSaveExistingEmail() {
        val user = User(
            login = "save-existing-email",
            email = "save-existing-email@example.com",
            password = RandomStringUtils.random(60),
            createdBy = SYSTEM_ACCOUNT,
            activated = true
        )

        userRepository.save(user).block()

        val anotherUser = User(
            login = "save-existing-email2",
            email = "save-existing-email2@example.com",
            password = RandomStringUtils.random(60),
            createdBy = SYSTEM_ACCOUNT,
            activated = true
        )

        userRepository.save(anotherUser).block()

        val userDTO = AdminUserDTO(
            login = "not-used",
            firstName = "firstname",
            lastName = "lastname",
            email = "save-existing-email2@example.com",
            activated = false,
            imageUrl = "http://placehold.it/50x50",
            langKey = DEFAULT_LANGUAGE,
            authorities = mutableSetOf(ADMIN)
        )

        accountWebTestClient.post().uri("/api/account")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(userDTO))
            .exchange()
            .expectStatus().isBadRequest

        val updatedUser = userRepository.findOneByLogin("save-existing-email").block()
        assertThat(updatedUser.email).isEqualTo("save-existing-email@example.com")
    }

    @Test
    @WithMockUser("save-existing-email-and-login")
    fun testSaveExistingEmailAndLogin() {
        val user = User(
            login = "save-existing-email-and-login",
            email = "save-existing-email-and-login@example.com",
            password = RandomStringUtils.random(60),
            createdBy = SYSTEM_ACCOUNT,
            activated = true
        )

        userRepository.save(user).block()

        val userDTO = AdminUserDTO(
            login = "not-used",
            firstName = "firstname",
            lastName = "lastname",
            email = "save-existing-email-and-login@example.com",
            activated = false,
            imageUrl = "http://placehold.it/50x50",
            langKey = DEFAULT_LANGUAGE,
            authorities = mutableSetOf(ADMIN)
        )
        // Mark here....
        accountWebTestClient.post().uri("/api/account")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(userDTO))
            .exchange()
            .expectStatus().isOk

        val updatedUser = userRepository.findOneByLogin("save-existing-email-and-login").block()
        assertThat(updatedUser.email).isEqualTo("save-existing-email-and-login@example.com")
    }

    @Test
    @WithMockUser("change-password-wrong-existing-password")
    fun testChangePasswordWrongExistingPassword() {
        val currentPassword = RandomStringUtils.random(60)
        val user = User(
            password = passwordEncoder.encode(currentPassword),
            login = "change-password-wrong-existing-password",
            createdBy = SYSTEM_ACCOUNT,
            email = "change-password-wrong-existing-password@example.com"
        )

        userRepository.save(user).block()

        accountWebTestClient.post().uri("/api/account/change-password")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(PasswordChangeDTO("1$currentPassword", "new password")))
            .exchange()
            .expectStatus().isBadRequest

        val updatedUser = userRepository.findOneByLogin("change-password-wrong-existing-password").block()
        assertThat(passwordEncoder.matches("new password", updatedUser.password)).isFalse
        assertThat(passwordEncoder.matches(currentPassword, updatedUser.password)).isTrue
    }

    @Test
    @WithMockUser("change-password")
    fun testChangePassword() {
        val currentPassword = RandomStringUtils.random(60)
        val user = User(
            password = passwordEncoder.encode(currentPassword),
            login = "change-password",
            createdBy = SYSTEM_ACCOUNT,
            email = "change-password@example.com"
        )

        userRepository.save(user).block()

        accountWebTestClient.post().uri("/api/account/change-password")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(PasswordChangeDTO(currentPassword, "new password")))
            .exchange()
            .expectStatus().isOk

        val updatedUser = userRepository.findOneByLogin("change-password").block()
        assertThat(passwordEncoder.matches("new password", updatedUser.password)).isTrue
    }

    @Test
    @WithMockUser("change-password-too-small")
    fun testChangePasswordTooSmall() {
        val currentPassword = RandomStringUtils.random(60)
        val user = User(
            password = passwordEncoder.encode(currentPassword),
            login = "change-password-too-small",
            createdBy = SYSTEM_ACCOUNT,
            email = "change-password-too-small@example.com"
        )

        userRepository.save(user).block()

        val newPassword = RandomStringUtils.random(ManagedUserVM.PASSWORD_MIN_LENGTH - 1)

        accountWebTestClient.post().uri("/api/account/change-password")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(PasswordChangeDTO(currentPassword, newPassword)))
            .exchange()
            .expectStatus().isBadRequest

        val updatedUser = userRepository.findOneByLogin("change-password-too-small").block()
        assertThat(updatedUser.password).isEqualTo(user.password)
    }

    @Test
    @WithMockUser("change-password-too-long")
    fun testChangePasswordTooLong() {
        val currentPassword = RandomStringUtils.random(60)
        val user = User(
            password = passwordEncoder.encode(currentPassword),
            login = "change-password-too-long",
            createdBy = SYSTEM_ACCOUNT,
            email = "change-password-too-long@example.com"
        )

        userRepository.save(user).block()

        val newPassword = RandomStringUtils.random(ManagedUserVM.PASSWORD_MAX_LENGTH + 1)

        accountWebTestClient.post().uri("/api/account/change-password")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(PasswordChangeDTO(currentPassword, newPassword)))
            .exchange()
            .expectStatus().isBadRequest

        val updatedUser = userRepository.findOneByLogin("change-password-too-long").block()
        assertThat(updatedUser.password).isEqualTo(user.password)
    }

    @Test
    @WithMockUser("change-password-empty")
    fun testChangePasswordEmpty() {
        val currentPassword = RandomStringUtils.random(60)
        val user = User(
            password = passwordEncoder.encode(currentPassword),
            login = "change-password-empty",
            createdBy = SYSTEM_ACCOUNT,
            email = "change-password-empty@example.com"
        )

        userRepository.save(user).block()

        accountWebTestClient.post().uri("/api/account/change-password")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(PasswordChangeDTO(currentPassword, "")))
            .exchange()
            .expectStatus().isBadRequest

        val updatedUser = userRepository.findOneByLogin("change-password-empty").block()
        assertThat(updatedUser.password).isEqualTo(user.password)
    }

    @Test
    fun testRequestPasswordReset() {
        val user = User(
            password = RandomStringUtils.random(60),
            activated = true,
            login = "password-reset",
            createdBy = SYSTEM_ACCOUNT,
            email = "password-reset@example.com"
        )

        userRepository.save(user).block()

        accountWebTestClient.post().uri("/api/account/reset-password/init")
            .bodyValue("password-reset@example.com")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun testRequestPasswordResetUpperCaseEmail() {
        val user = User(
            password = RandomStringUtils.random(60),
            activated = true,
            login = "password-reset-upper-case",
            createdBy = SYSTEM_ACCOUNT,
            email = "password-reset-upper-case@example.com"
        )

        userRepository.save(user).block()

        accountWebTestClient.post().uri("/api/account/reset-password/init")
            .bodyValue("password-reset-upper-case@EXAMPLE.COM")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun testRequestPasswordResetWrongEmail() {
        accountWebTestClient.post().uri("/api/account/reset-password/init")
            .bodyValue("password-reset-wrong-email@example.com")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    @Throws(Exception::class)
    fun testFinishPasswordReset() {
        val user = User(
            password = RandomStringUtils.random(60),
            login = "finish-password-reset",
            email = "finish-password-reset@example.com",
            resetDate = Instant.now().plusSeconds(60),
            createdBy = SYSTEM_ACCOUNT,
            resetKey = "reset key"
        )

        userRepository.save(user).block()

        val keyAndPassword = KeyAndPasswordVM(key = user.resetKey, newPassword = "new password")

        accountWebTestClient.post().uri("/api/account/reset-password/finish")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(keyAndPassword))
            .exchange()
            .expectStatus().isOk

        val updatedUser = userRepository.findOneByLogin(user.login!!).block()
        assertThat(passwordEncoder.matches(keyAndPassword.newPassword, updatedUser.password)).isTrue
    }

    @Test
    @Throws(Exception::class)
    fun testFinishPasswordResetTooSmall() {
        val user = User(
            password = RandomStringUtils.random(60),
            login = "finish-password-reset-too-small",
            email = "finish-password-reset-too-small@example.com",
            resetDate = Instant.now().plusSeconds(60),
            createdBy = SYSTEM_ACCOUNT,
            resetKey = "reset key too small"
        )

        userRepository.save(user).block()

        val keyAndPassword = KeyAndPasswordVM(key = user.resetKey, newPassword = "foo")

        accountWebTestClient.post().uri("/api/account/reset-password/finish")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(keyAndPassword))
            .exchange()
            .expectStatus().isBadRequest

        val updatedUser = userRepository.findOneByLogin(user.login!!).block()
        assertThat(passwordEncoder.matches(keyAndPassword.newPassword, updatedUser.password)).isFalse
    }

    @Test
    @Throws(Exception::class)
    fun testFinishPasswordResetWrongKey() {
        val keyAndPassword = KeyAndPasswordVM(key = "wrong reset key", newPassword = "new password")

        accountWebTestClient.post().uri("/api/account/reset-password/finish")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(keyAndPassword))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
