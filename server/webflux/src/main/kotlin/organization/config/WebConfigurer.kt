package organization.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.web.reactive.ResourceHandlerRegistrationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver
import org.springframework.data.web.ReactiveSortHandlerMethodArgumentResolver
import org.springframework.util.CollectionUtils
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.server.WebExceptionHandler
import org.zalando.problem.spring.webflux.advice.ProblemExceptionHandler
import org.zalando.problem.spring.webflux.advice.ProblemHandling
import tech.jhipster.config.JHipsterConstants
import tech.jhipster.config.JHipsterProperties
import tech.jhipster.web.filter.reactive.CachingHttpHeadersFilter
import java.util.concurrent.TimeUnit

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
class WebConfigurer(
    private val jHipsterProperties: JHipsterProperties
) : WebFluxConfigurer {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun corsFilter(): CorsWebFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = jHipsterProperties.cors
        if (!CollectionUtils.isEmpty(config.allowedOrigins) || ! CollectionUtils.isEmpty(config.allowedOriginPatterns)) {
            log.debug("Registering CORS filter")
            source.apply {
                registerCorsConfiguration("/api/**", config)
                registerCorsConfiguration("/management/**", config)
                registerCorsConfiguration("/v3/api-docs", config)
                registerCorsConfiguration("/swagger-ui/**", config)
            }
        }
        return CorsWebFilter(source)
    }

    // TODO: remove when this is supported in spring-data / spring-boot
    @Bean
    fun reactivePageableHandlerMethodArgumentResolver() = ReactivePageableHandlerMethodArgumentResolver()

    // TODO: remove when this is supported in spring-boot
    @Bean
    fun reactiveSortHandlerMethodArgumentResolver() = ReactiveSortHandlerMethodArgumentResolver()

    @Bean
    @Order(-2) // The handler must have precedence over WebFluxResponseStatusExceptionHandler and Spring Boot's ErrorWebExceptionHandler
    fun problemExceptionHandler(mapper: ObjectMapper, problemHandling: ProblemHandling): WebExceptionHandler {
        return ProblemExceptionHandler(mapper, problemHandling)
    }

    @Bean
    fun registrationCustomizer(): ResourceHandlerRegistrationCustomizer {
        // Disable built-in cache control to use our custom filter instead
        return ResourceHandlerRegistrationCustomizer { registration -> registration.setCacheControl(null) }
    }

    @Bean
    @Profile(JHipsterConstants.SPRING_PROFILE_PRODUCTION)
    fun cachingHttpHeadersFilter(): CachingHttpHeadersFilter {
        // Use a cache filter that only match selected paths
        return CachingHttpHeadersFilter(TimeUnit.DAYS.toMillis(jHipsterProperties.http.cache.timeToLiveInDays.toLong()))
    }
}
