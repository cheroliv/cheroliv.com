package organization.security.jwt

import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.test.util.ReflectionTestUtils
import organization.management.SecurityMetersService
import organization.security.USER
import reactor.core.publisher.Mono
import tech.jhipster.config.JHipsterProperties

class JWTFilterTest {

    private lateinit var tokenProvider: TokenProvider

    private lateinit var jwtFilter: JWTFilter

    @BeforeEach
    fun setup() {
        val jHipsterProperties = JHipsterProperties()
        val base64Secret = "fd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8"
        jHipsterProperties.security.authentication.jwt.base64Secret = base64Secret
        val securityMetersService = SecurityMetersService(SimpleMeterRegistry())
        tokenProvider = TokenProvider(jHipsterProperties, securityMetersService)
        ReflectionTestUtils.setField(tokenProvider, "key", Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret)))

        ReflectionTestUtils.setField(tokenProvider, "tokenValidityInMilliseconds", 60000)
        jwtFilter = JWTFilter(tokenProvider)
    }

    @Test
    fun testJWTFilter() {
        val authentication = UsernamePasswordAuthenticationToken(
            "test-user",
            "test-password",
            listOf(SimpleGrantedAuthority(USER))
        )
        val jwt = tokenProvider.createToken(authentication, false)
        val request = MockServerHttpRequest
            .get("/api/test")
            .header(JWTFilter.AUTHORIZATION_HEADER, "Bearer $jwt")
        val exchange = MockServerWebExchange.from(request)
        jwtFilter.filter(exchange) {
            Mono.deferContextual { Mono.just(it) }
                .flatMap { ReactiveSecurityContextHolder.getContext() }
                .map(SecurityContext::getAuthentication)
                .doOnSuccess { auth -> assertThat(auth.name).isEqualTo("test-user") }
                .doOnSuccess { auth -> assertThat(auth.credentials.toString()).hasToString(jwt) }
                .then()
        }.block()
    }

    @Test
    fun testJWTFilterInvalidToken() {
        val jwt = "wrong_jwt"
        val request = MockServerHttpRequest
            .get("/api/test")
            .header(JWTFilter.AUTHORIZATION_HEADER, "Bearer $jwt")
        val exchange = MockServerWebExchange.from(request)
        jwtFilter.filter(exchange) {
            Mono.deferContextual { Mono.just(it) }
                .flatMap { ReactiveSecurityContextHolder.getContext() }
                .map(SecurityContext::getAuthentication)
                .doOnSuccess { auth -> assertThat(auth).isNull() }
                .then()
        }.block()
    }

    @Test
    fun testJWTFilterMissingAuthorization() {
        val request = MockServerHttpRequest
            .get("/api/test")
        val exchange = MockServerWebExchange.from(request)
        jwtFilter.filter(exchange) {
            Mono.deferContextual { Mono.just(it) }
                .flatMap { ReactiveSecurityContextHolder.getContext() }
                .map(SecurityContext::getAuthentication)
                .doOnSuccess { auth -> assertThat(auth).isNull() }
                .then()
        }.block()
    }

    @Test
    fun testJWTFilterMissingToken() {
        val request = MockServerHttpRequest
            .get("/api/test")
            .header(JWTFilter.AUTHORIZATION_HEADER, "Bearer ")
        val exchange = MockServerWebExchange.from(request)
        jwtFilter.filter(exchange) {
            Mono.deferContextual { Mono.just(it) }
                .flatMap { ReactiveSecurityContextHolder.getContext() }
                .map(SecurityContext::getAuthentication)
                .doOnSuccess { auth -> assertThat(auth).isNull() }
                .then()
        }.block()
    }

    @Test
    fun testJWTFilterWrongScheme() {
        val authentication = UsernamePasswordAuthenticationToken(
            "test-user",
            "test-password",
            listOf(SimpleGrantedAuthority(USER))
        )
        val jwt = tokenProvider.createToken(authentication, false)
        val request = MockServerHttpRequest
            .get("/api/test")
            .header(JWTFilter.AUTHORIZATION_HEADER, "Basic $jwt")
        val exchange = MockServerWebExchange.from(request)
        jwtFilter.filter(exchange) {
            Mono.deferContextual { Mono.just(it) }
                .flatMap { ReactiveSecurityContextHolder.getContext() }
                .map(SecurityContext::getAuthentication)
                .doOnSuccess { auth -> assertThat(auth).isNull() }
                .then()
        }.block()
    }
}
