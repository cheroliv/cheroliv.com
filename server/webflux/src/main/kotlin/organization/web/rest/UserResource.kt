package organization.web.rest

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriComponentsBuilder
import organization.config.LOGIN_REGEX
import organization.domain.User
import organization.repository.UserRepository
import organization.security.ADMIN
import organization.service.MailService
import organization.service.UserService
import organization.service.dto.AdminUserDTO
import organization.web.rest.errors.BadRequestAlertException
import organization.web.rest.errors.EmailAlreadyUsedException
import organization.web.rest.errors.LoginAlreadyUsedException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import tech.jhipster.web.util.HeaderUtil
import tech.jhipster.web.util.PaginationUtil
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid
import javax.validation.constraints.Pattern

/**
 * REST controller for managing users.
 *
 * This class accesses the [User] entity, and needs to fetch its collection of authorities.
 *
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 *
 * We use a View Model and a DTO for 3 reasons:
 *
 * * We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.
 * *  Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).
 * *  As this manages users, for security reasons, we'd rather have a DTO layer.
 *
 * Another option would be to have a specific JPA entity graph to handle this case.
 */
@RestController
@RequestMapping("/api/admin")
class UserResource(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val mailService: MailService
) {

    companion object {
        private val ALLOWED_ORDERED_PROPERTIES = arrayOf("id", "login", "firstName", "lastName", "email", "activated", "langKey", "createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate")
    }

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jhipster.clientApp.name}")
    private val applicationName: String? = null

    /**
     * `POST  /admin/users`  : Creates a new user.
     *
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     *
     * @param userDTO the user to create.
     * @return the `ResponseEntity` with status `201 (Created)` and with body the new user, or with status `400 (Bad Request)` if the login or email is already in use.
     * @throws BadRequestAlertException `400 (Bad Request)` if the login or email is already in use.
     */
    @PostMapping("/users")
    @PreAuthorize("hasAuthority(\"$ADMIN\")")
    fun createUser(@Valid @RequestBody userDTO: AdminUserDTO): Mono<ResponseEntity<User>> {
        log.debug("REST request to save User : $userDTO")

        if (userDTO.id != null) {
            throw BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists")
            // Lowercase the user login before comparing with database
        }
        return userRepository.findOneByLogin(userDTO.login!!.toLowerCase())
            .hasElement()
            .flatMap { loginExists ->
                if (loginExists!!) {
                    throw LoginAlreadyUsedException()
                }
                userRepository.findOneByEmailIgnoreCase(userDTO.email!!)
            }
            .hasElement()
            .flatMap { emailExists ->
                if (emailExists!!) {
                    throw EmailAlreadyUsedException()
                }
                userService.createUser(userDTO)
            }
            .doOnSuccess(mailService::sendCreationEmail)
            .map { user ->
                try {
                    ResponseEntity.created(URI("/api/admin/users/${user.login}"))
                        .headers(HeaderUtil.createAlert(applicationName, "userManagement.created", user.login))
                        .body(user)
                } catch (e: URISyntaxException) {
                    throw RuntimeException(e)
                }
            }
    }

    /**
     * `PUT /admin/users` : Updates an existing User.
     *
     * @param userDTO the user to update.
     * @return the `ResponseEntity` with status `200 (OK)` and with body the updated user.
     * @throws EmailAlreadyUsedException `400 (Bad Request)` if the email is already in use.
     * @throws LoginAlreadyUsedException `400 (Bad Request)` if the login is already in use.
     */
    @PutMapping("/users")
    @PreAuthorize("hasAuthority(\"$ADMIN\")")
    fun updateUser(@Valid @RequestBody userDTO: AdminUserDTO): Mono<ResponseEntity<AdminUserDTO>> {
        log.debug("REST request to update User : $userDTO")
        return userRepository.findOneByEmailIgnoreCase(userDTO.email!!)
            .filter { user -> user.id != userDTO.id }
            .hasElement()
            .flatMap { emailExists ->
                if (emailExists!!) {
                    throw EmailAlreadyUsedException()
                }
                userRepository.findOneByLogin(userDTO.login!!.toLowerCase())
            }
            .filter { user -> user.id != userDTO.id }
            .hasElement()
            .flatMap { loginExists ->
                if (loginExists!!) {
                    throw LoginAlreadyUsedException()
                }
                userService.updateUser(userDTO)
            }
            .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
            .map { user ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createAlert(applicationName, "userManagement.updated", userDTO.login))
                    .body(user)
            }
    }

    /**
     * `GET /admin/users` : get all users with all the details - calling this are only allowed for the administrators.
     *
     * @param request a [ServerHttpRequest] request.
     * @param pageable the pagination information.
     * @return the `ResponseEntity` with status `200 (OK)` and with body all users.
     */
    @GetMapping("/users")
    @PreAuthorize("hasAuthority(\"$ADMIN\")")
    fun getAllUsers(
        request: ServerHttpRequest,
        @org.springdoc.api.annotations.ParameterObject pageable: Pageable
    ): Mono<ResponseEntity<Flux<AdminUserDTO>>> {
        log.debug("REST request to get all User for an admin")
        if (!onlyContainsAllowedProperties(pageable)) {
            return Mono.just(ResponseEntity.badRequest().build())
        }

        return userService.countManagedUsers()
            .map { total -> PageImpl(mutableListOf<AdminUserDTO>(), pageable, total!!) }
            .map { page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page) }
            .map { headers -> ResponseEntity.ok().headers(headers).body(userService.getAllManagedUsers(pageable)) }
    }
    private fun onlyContainsAllowedProperties(pageable: Pageable) =
        pageable.sort.map(Sort.Order::getProperty).all { ALLOWED_ORDERED_PROPERTIES.contains(it) }

    /**
     * `GET /admin/users/:login` : get the "login" user.
     *
     * @param login the login of the user to find.
     * @return the `ResponseEntity` with status `200 (OK)` and with body the "login" user, or with status `404 (Not Found)`.
     */
    @GetMapping("/users/{login}")
    @PreAuthorize("hasAuthority(\"$ADMIN\")")
    fun getUser(@PathVariable login: String): Mono<AdminUserDTO> {
        log.debug("REST request to get User : $login")
        return userService.getUserWithAuthoritiesByLogin(login)
            .map { AdminUserDTO(it) }
            .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
    }

    /**
     * `DELETE /admin/users/:login` : delete the "login" User.
     *
     * @param login the login of the user to delete.
     * @return the `ResponseEntity` with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/users/{login}")
    @PreAuthorize("hasAuthority(\"$ADMIN\")")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable @Pattern(regexp = LOGIN_REGEX) login: String): Mono<ResponseEntity<Void>> {
        log.debug("REST request to delete User: $login")
        return userService.deleteUser(login)
            .map { ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, "userManagement.deleted", login)).build<Void>() }
    }
}
