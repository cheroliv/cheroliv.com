package organization.repository

import org.apache.commons.beanutils.BeanComparator
import org.springframework.data.domain.*
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import organization.domain.Authority
import organization.domain.User
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import reactor.util.function.Tuples
import java.time.LocalDateTime
import java.util.Optional

/**
 * Spring Data R2DBC repository for the {@link User} entity.
 */
@Repository
interface UserRepository : R2dbcRepository<User, Long>, UserRepositoryInternal {

    fun findOneByActivationKey(activationKey: String): Mono<User>

    fun findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(dateTime: LocalDateTime): Flux<User>

    fun findOneByResetKey(resetKey: String): Mono<User>

    fun findOneByEmailIgnoreCase(email: String): Mono<User>

    fun findOneByLogin(login: String): Mono<User>

    fun findAllByIdNotNull(pageable: Pageable): Flux<User>
    fun findAllByIdNotNullAndActivatedIsTrue(pageable: Pageable): Flux<User>

    override fun count(): Mono<Long>

    @Query("INSERT INTO jhi_user_authority VALUES(:userId, :authority)")
    fun saveUserAuthority(userId: Long, authority: String): Mono<Void>

    @Query("DELETE FROM jhi_user_authority")
    fun deleteAllUserAuthorities(): Mono<Void>

    @Query("DELETE FROM jhi_user_authority WHERE user_id = :userId")
    fun deleteUserAuthorities(userId: Long): Mono<Void>
}

interface DeleteExtended<T> {
    fun delete(user: T): Mono<Void>
}

interface UserRepositoryInternal : DeleteExtended<User> {

    fun findOneWithAuthoritiesByLogin(login: String): Mono<User>

    fun findOneWithAuthoritiesByEmailIgnoreCase(email: String): Mono<User>

    fun findAllWithAuthorities(pageable: Pageable): Flux<User>
}

class UserRepositoryInternalImpl(val db: DatabaseClient, val r2dbcEntityTemplate: R2dbcEntityTemplate, val r2dbcConverter: R2dbcConverter) : UserRepositoryInternal {

    override fun findOneWithAuthoritiesByLogin(login: String): Mono<User> {
        return findOneWithAuthoritiesBy("login", login)
    }

    override fun findOneWithAuthoritiesByEmailIgnoreCase(email: String): Mono<User> {
        return findOneWithAuthoritiesBy("email", email.toLowerCase())
    }

    override fun findAllWithAuthorities(pageable: Pageable): Flux<User> {
        val property = pageable.sort.map(Sort.Order::getProperty).first() ?: "id"
        val direction = pageable.sort.map(Sort.Order::getDirection).first() ?: Sort.DEFAULT_DIRECTION
        val comparator = if (direction == Sort.DEFAULT_DIRECTION) { BeanComparator(property) } else { BeanComparator<Any>(property).reversed() }
        val page = pageable.pageNumber
        val size = pageable.pageSize

        return db
            .sql("SELECT * FROM jhi_user u LEFT JOIN jhi_user_authority ua ON u.id=ua.user_id")
            .map { row, metadata ->
                return@map Tuples.of(
                    r2dbcConverter.read(User::class.java, row, metadata),
                    Optional.ofNullable(row.get("authority_name", String::class.java))
                )
            }.all()
            .groupBy { it.t1.login }
            .flatMap { it.collectList().map { t -> updateUserWithAuthorities(t[0].t1, t) } }
            .sort(comparator)
            .skip((page * size).toLong())
            .take(size.toLong())
    }

    override fun delete(user: User): Mono<Void> {
        return db.sql("DELETE FROM jhi_user_authority WHERE user_id = :userId")
            .bind("userId", user.id)
            .then()
            .then(
                r2dbcEntityTemplate.delete(User::class.java)
                    .matching(query(where("id").`is`(user.id))).all()
                    .then()
            )
    }

    private fun findOneWithAuthoritiesBy(fieldName: String, fieldValue: Any): Mono<User> {
        return db.sql("SELECT * FROM jhi_user u LEFT JOIN jhi_user_authority ua ON u.id=ua.user_id WHERE u.$fieldName = :$fieldName")
            .bind(fieldName, fieldValue)
            .map { row, metadata ->
                return@map Tuples.of(
                    r2dbcConverter.read(User::class.java, row, metadata),
                    Optional.ofNullable(row.get("authority_name", String::class.java))
                )
            }.all()
            .collectList()
            .filter { it.isNotEmpty() }
            .map { l -> updateUserWithAuthorities(l[0].t1, l) }
    }

    private fun updateUserWithAuthorities(user: User, tuples: List<Tuple2<User, Optional<String>>>): User {
        user.authorities = tuples.filter { it.t2.isPresent }
            .map {
                val authority = Authority()
                authority.name = it.t2.get()
                authority
            }.toMutableSet()
        return user
    }
}
