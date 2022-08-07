package organization.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter.Mode
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers
import org.zalando.problem.spring.webflux.advice.security.SecurityProblemSupport
import organization.security.ADMIN
import organization.security.jwt.JWTFilter
import organization.security.jwt.TokenProvider
import organization.web.filter.SpaWebFilter
import tech.jhipster.config.JHipsterProperties

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Import(SecurityProblemSupport::class)
class SecurityConfiguration(
    private val userDetailsService: ReactiveUserDetailsService,
    private val tokenProvider: TokenProvider,
    private val jHipsterProperties: JHipsterProperties,
    private val problemSupport: SecurityProblemSupport
) {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun reactiveAuthenticationManager() =
        UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService).apply {
            setPasswordEncoder(passwordEncoder())
        }

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        // @formatter:off
        http
            .securityMatcher(
                NegatedServerWebExchangeMatcher(
                    OrServerWebExchangeMatcher(
                        pathMatchers("/app/**", "/i18n/**", "/content/**", "/swagger-ui/**", "/v3/api-docs/**", "/test/**"),
                        pathMatchers(HttpMethod.OPTIONS, "/**")
                    )
                )
            )
            .csrf()
            .disable()
            .addFilterAt(SpaWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .addFilterAt(JWTFilter(tokenProvider), SecurityWebFiltersOrder.HTTP_BASIC)
            .authenticationManager(reactiveAuthenticationManager())
            .exceptionHandling()
            .accessDeniedHandler(problemSupport)
            .authenticationEntryPoint(problemSupport)
            .and()
            .headers()
            .contentSecurityPolicy(jHipsterProperties.security.contentSecurityPolicy)
            .and()
            .referrerPolicy(ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
            .and()
            .permissionsPolicy().policy("camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()")
            .and()
            .frameOptions().mode(Mode.DENY)
            .and()
            .authorizeExchange()
            .pathMatchers("/").permitAll()
            .pathMatchers("/*.*").permitAll()
            .pathMatchers("/api/authenticate").permitAll()
            .pathMatchers("/api/register").permitAll()
            .pathMatchers("/api/activate").permitAll()
            .pathMatchers("/api/account/reset-password/init").permitAll()
            .pathMatchers("/api/account/reset-password/finish").permitAll()
            .pathMatchers("/api/auth-info").permitAll()
            .pathMatchers("/api/**").authenticated()
            .pathMatchers("/services/**").authenticated()
            .pathMatchers("/management/health").permitAll()
            .pathMatchers("/management/info").permitAll()
            .pathMatchers("/management/prometheus").permitAll()
            .pathMatchers("/management/**").hasAuthority(ADMIN)
        // @formatter:on
        return http.build()
    }
}
