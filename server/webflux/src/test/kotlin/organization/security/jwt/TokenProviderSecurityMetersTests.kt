package organization.security.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.test.util.ReflectionTestUtils
import organization.management.SecurityMetersService
import organization.security.ANONYMOUS
import tech.jhipster.config.JHipsterProperties
import java.security.Key
import java.util.Date

private const val ONE_MINUTE = 60000
private const val INVALID_TOKENS_METER_EXPECTED_NAME = "security.authentication.invalid-tokens"

class TokenProviderSecurityMetersTests {

    private lateinit var meterRegistry: MeterRegistry

    private lateinit var tokenProvider: TokenProvider

    @BeforeEach
    fun setup() {
        val jHipsterProperties = JHipsterProperties()
        val base64Secret = "fd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8"
        jHipsterProperties.security.authentication.jwt.base64Secret = base64Secret

        meterRegistry = SimpleMeterRegistry()

        val securityMetersService = SecurityMetersService(meterRegistry)

        tokenProvider = TokenProvider(jHipsterProperties, securityMetersService)
        val key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret))

        ReflectionTestUtils.setField(tokenProvider, "key", key)
        ReflectionTestUtils.setField(tokenProvider, "tokenValidityInMilliseconds", ONE_MINUTE)
    }

    @Test
    fun testValidTokenShouldNotCountAnything() {
        val counters = meterRegistry.find(INVALID_TOKENS_METER_EXPECTED_NAME).counters()

        assertThat(aggregate(counters)).isZero()

        val validToken = createValidToken()

        tokenProvider.validateToken(validToken)

        assertThat(aggregate(counters)).isZero()
    }

    @Test
    fun testTokenExpiredCount() {
        assertThat(
            meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME)
                .tag("cause", "expired")
                .counter().count()
        ).isZero()

        val expiredToken = createExpiredToken()

        tokenProvider.validateToken(expiredToken)

        assertThat(
            meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME)
                .tag("cause", "expired")
                .counter().count()
        ).isEqualTo(1.0)
    }

    @Test
    fun testTokenUnsupportedCount() {
        assertThat(
            meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME)
                .tag("cause", "unsupported")
                .counter().count()
        ).isZero()

        val unsupportedToken = createUnsupportedToken()

        tokenProvider.validateToken(unsupportedToken)

        assertThat(
            meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME)
                .tag("cause", "unsupported")
                .counter().count()
        ).isEqualTo(1.0)
    }

    @Test
    fun testTokenSignatureInvalidCount() {
        assertThat(
            meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME)
                .tag("cause", "invalid-signature")
                .counter().count()
        ).isZero()

        val tokenWithDifferentSignature = createTokenWithDifferentSignature()

        tokenProvider.validateToken(tokenWithDifferentSignature)

        assertThat(
            meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME)
                .tag("cause", "invalid-signature")
                .counter().count()
        ).isEqualTo(1.0)
    }

    @Test
    fun testTokenMalformedCount() {
        assertThat(
            meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME)
                .tag("cause", "malformed")
                .counter().count()
        ).isZero()

        val malformedToken = createMalformedToken()

        tokenProvider.validateToken(malformedToken)

        assertThat(
            meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME)
                .tag("cause", "malformed")
                .counter().count()
        ).isEqualTo(1.0)
    }

    private fun createValidToken(): String {
        val authentication = createAuthentication()

        return tokenProvider.createToken(authentication, false)
    }

    private fun createExpiredToken(): String {
        ReflectionTestUtils.setField(tokenProvider, "tokenValidityInMilliseconds", -ONE_MINUTE)

        val authentication = createAuthentication()

        return tokenProvider.createToken(authentication, false)
    }

    private fun createAuthentication(): Authentication {
        val authorities = listOf(SimpleGrantedAuthority(ANONYMOUS))
        return UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities)
    }

    private fun createUnsupportedToken(): String {
        val key = ReflectionTestUtils.getField(tokenProvider, "key") as Key

        return Jwts.builder().setPayload("payload").signWith(key, SignatureAlgorithm.HS256).compact()
    }

    private fun createMalformedToken(): String {
        val validToken = createValidToken()

        return "X$validToken"
    }

    private fun createTokenWithDifferentSignature(): String {
        val otherKey = Keys.hmacShaKeyFor(
            Decoders.BASE64.decode("Xfd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8")
        )

        return Jwts
            .builder()
            .setSubject("anonymous")
            .signWith(otherKey, SignatureAlgorithm.HS512)
            .setExpiration(Date(Date().time + ONE_MINUTE))
            .compact()
    }

    private fun aggregate(counters: Collection<Counter>): Double {
        return counters.sumOf(Counter::count)
    }
}
