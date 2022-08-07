package organization.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder

/**
 * Test class for the {@link SecurityUtils} utility class.
 */
class SecurityUtilsUnitTest {
    @Test
    fun testgetCurrentUserLogin() {
        val login = getCurrentUserLogin()
            .subscriberContext(
                ReactiveSecurityContextHolder.withAuthentication(
                    UsernamePasswordAuthenticationToken("admin", "admin")
                )
            )
            .block()
        assertThat(login).isEqualTo("admin")
    }

    @Test
    fun testGetCurrentUserJWT() {
        val jwt = getCurrentUserJWT()
            .subscriberContext(
                ReactiveSecurityContextHolder.withAuthentication(
                    UsernamePasswordAuthenticationToken("admin", "token")
                )
            )
            .block()
        assertThat(jwt).isEqualTo("token")
    }

    @Test
    fun testIsAuthenticated() {
        val isAuthenticated = isAuthenticated()
            .subscriberContext(
                ReactiveSecurityContextHolder.withAuthentication(
                    UsernamePasswordAuthenticationToken("admin", "admin")
                )
            )
            .block()
        assertThat(isAuthenticated!!).isTrue
    }

    @Test
    fun testAnonymousIsNotAuthenticated() {
        val authorities = mutableListOf(SimpleGrantedAuthority(ANONYMOUS))
        val isAuthenticated = isAuthenticated()
            .subscriberContext(
                ReactiveSecurityContextHolder.withAuthentication(
                    UsernamePasswordAuthenticationToken("admin", "admin", authorities)
                )
            )
            .block()
        assertThat(isAuthenticated!!).isFalse
    }

    @Test
    fun testHasCurrentUserAnyOfAuthorities() {
        val authorities = listOf(SimpleGrantedAuthority(USER))
        val context = ReactiveSecurityContextHolder.withAuthentication(
            UsernamePasswordAuthenticationToken("admin", "admin", authorities)
        )
        var hasCurrentUserThisAuthority = hasCurrentUserAnyOfAuthorities(USER, ADMIN)
            .subscriberContext(context)
            .block()
        assertThat(hasCurrentUserThisAuthority).isTrue

        hasCurrentUserThisAuthority = hasCurrentUserAnyOfAuthorities(ANONYMOUS, ADMIN)
            .subscriberContext(context)
            .block()
        assertThat(hasCurrentUserThisAuthority).isFalse
    }

    @Test
    fun testHasCurrentUserNoneOfAuthorities() {
        val authorities = listOf(SimpleGrantedAuthority(USER))
        val context = ReactiveSecurityContextHolder.withAuthentication(
            UsernamePasswordAuthenticationToken("admin", "admin", authorities)
        )
        var hasCurrentUserThisAuthority = hasCurrentUserNoneOfAuthorities(USER, ADMIN)
            .subscriberContext(context)
            .block()
        assertThat(hasCurrentUserThisAuthority).isFalse

        hasCurrentUserThisAuthority = hasCurrentUserNoneOfAuthorities(ANONYMOUS, ADMIN)
            .subscriberContext(context)
            .block()
        assertThat(hasCurrentUserThisAuthority).isTrue
    }

    @Test
    fun testHasCurrentUserThisAuthority() {
        val authorities = mutableListOf(SimpleGrantedAuthority(USER))
        val context = ReactiveSecurityContextHolder.withAuthentication(
            UsernamePasswordAuthenticationToken("admin", "admin", authorities)
        )
        var hasCurrentUserThisAuthority = hasCurrentUserThisAuthority(USER)
            .subscriberContext(context)
            .block()
        assertThat(hasCurrentUserThisAuthority!!).isTrue

        hasCurrentUserThisAuthority = hasCurrentUserThisAuthority(ADMIN)
            .subscriberContext(context)
            .block()
        assertThat(hasCurrentUserThisAuthority!!).isFalse
    }
}
