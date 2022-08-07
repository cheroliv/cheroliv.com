package organization.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import organization.aop.logging.LoggingAspect
import tech.jhipster.config.JHipsterConstants

@Configuration
@EnableAspectJAutoProxy
class LoggingAspectConfiguration {

    @Bean
    @Profile(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)
    fun loggingAspect(env: Environment) = LoggingAspect(env)
}
