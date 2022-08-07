package organization.web.rest

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import organization.repository.UserRepository
import organization.security.getCurrentUserLogin
import organization.service.MailService
import organization.service.UserService
import organization.service.dto.AdminUserDTO
import organization.service.dto.PasswordChangeDTO
import organization.web.rest.errors.EmailAlreadyUsedException
import organization.web.rest.errors.InvalidPasswordException
import organization.web.rest.errors.LoginAlreadyUsedException
import organization.web.rest.vm.KeyAndPasswordVM
import organization.web.rest.vm.ManagedUserVM
import reactor.core.publisher.Mono
import java.security.Principal
import javax.validation.Valid

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
class AccountResource(
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val mailService: MailService
) {

    internal class AccountResourceException(message: String) : RuntimeException(message)

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * `POST  /register` : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @throws InvalidPasswordException `400 (Bad Request)` if the password is incorrect.
     * @throws EmailAlreadyUsedException `400 (Bad Request)` if the email is already used.
     * @throws LoginAlreadyUsedException `400 (Bad Request)` if the login is already used.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerAccount(@Valid @RequestBody managedUserVM: ManagedUserVM): Mono<Void> {
        if (isPasswordLengthInvalid(managedUserVM.password)) {
            throw InvalidPasswordException()
        }
        return userService.registerUser(managedUserVM, managedUserVM.password!!)
            .doOnSuccess(mailService::sendActivationEmail)
            .then()
    }

    /**
     * `GET  /activate` : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException `500 (Internal Server Error)` if the user couldn't be activated.
     */
    @GetMapping("/activate")
    fun activateAccount(@RequestParam(value = "key") key: String): Mono<Void> {
        return userService.activateRegistration(key)
            .switchIfEmpty(Mono.error(AccountResourceException("No user was found for this activation key")))
            .then()
    }

    /**
     * `GET  /authenticate` : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    fun isAuthenticated(request: ServerWebExchange): Mono<String?> {
        log.debug("REST request to check if the current user is authenticated")
        return request.getPrincipal<Principal>().map(Principal::getName)
    }

    /**
     * `GET  /account` : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException `500 (Internal Server Error)` if the user couldn't be returned.
     */
    @GetMapping("/account")
    fun getAccount(): Mono<AdminUserDTO> =
        userService.getUserWithAuthorities()
            .map { AdminUserDTO(it) }
            .switchIfEmpty(Mono.error(AccountResourceException("User could not be found")))

    /**
     * POST  /account : update the current user information.
     *
     * @param userDTO the current user information
     * @throws EmailAlreadyUsedException `400 (Bad Request)` if the email is already used.
     * @throws RuntimeException `500 (Internal Server Error)` if the user login wasn't found.
     */
    @PostMapping("/account")
    fun saveAccount(@Valid @RequestBody userDTO: AdminUserDTO): Mono<Void> =
        getCurrentUserLogin()
            .switchIfEmpty(Mono.error(AccountResourceException("Current user login not found")))
            .flatMap { userLogin ->
                userRepository.findOneByEmailIgnoreCase(userDTO.email!!)
                    .filter { existingUser -> !existingUser.login.equals(userLogin, ignoreCase = true) }
                    .hasElement()
                    .flatMap { emailExists ->
                        if (emailExists!!) {
                            throw EmailAlreadyUsedException()
                        }
                        userRepository.findOneByLogin(userLogin)
                    }
            }
            .switchIfEmpty(Mono.error(AccountResourceException("User could not be found")))
            .flatMap {
                userService.updateUser(
                    userDTO.firstName, userDTO.lastName, userDTO.email, userDTO.langKey, userDTO.imageUrl
                )
            }

    /**
     * POST  /account/change-password : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException `400 (Bad Request)` if the new password is incorrect.
     */
    @PostMapping(path = ["/account/change-password"])
    fun changePassword(@RequestBody passwordChangeDto: PasswordChangeDTO): Mono<Void> {
        if (isPasswordLengthInvalid(passwordChangeDto.newPassword)) {
            throw InvalidPasswordException()
        }
        return userService.changePassword(passwordChangeDto.currentPassword!!, passwordChangeDto.newPassword!!)
    }

    /**
     * POST   /account/reset-password/init : Send an email to reset the password of the user
     *
     * @param mail the mail of the user
     */
    @PostMapping(path = ["/account/reset-password/init"])
    fun requestPasswordReset(@RequestBody mail: String): Mono<Void> =
        userService.requestPasswordReset(mail)
            .doOnSuccess {
                if (it != null) {
                    mailService.sendPasswordResetMail(it)
                } else {
                    // Pretend the request has been successful to prevent checking which emails really exist
                    // but log that an invalid attempt has been made
                    log.warn("Password reset request for non existing email")
                }
            }.then()

    /**
     * `POST   /account/reset-password/finish` : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException `400 (Bad Request)` if the password is incorrect.
     * @throws RuntimeException `500 (Internal Server Error)` if the password could not be reset.
     */
    @PostMapping(path = ["/account/reset-password/finish"])
    fun finishPasswordReset(@RequestBody keyAndPassword: KeyAndPasswordVM): Mono<Void> {
        if (isPasswordLengthInvalid(keyAndPassword.newPassword)) {
            throw InvalidPasswordException()
        }
        return userService.completePasswordReset(keyAndPassword.newPassword!!, keyAndPassword.key!!)
            .switchIfEmpty(Mono.error(AccountResourceException("No user was found for this reset key")))
            .then()
    }
}

private fun isPasswordLengthInvalid(password: String?) = password.isNullOrEmpty() || password.length < ManagedUserVM.PASSWORD_MIN_LENGTH || password.length > ManagedUserVM.PASSWORD_MAX_LENGTH
