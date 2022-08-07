package organization

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.core.env.Environment
import organization.config.ApplicationProperties
import tech.jhipster.config.DefaultProfileUtil
import tech.jhipster.config.JHipsterConstants
import java.net.InetAddress
import java.net.UnknownHostException
import javax.annotation.PostConstruct

@SpringBootApplication
@EnableConfigurationProperties(LiquibaseProperties::class, ApplicationProperties::class)
class OrganizationApp(private val env: Environment) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Initializes organization.
     *
     * Spring profiles can be configured with a program argument --spring.profiles.active=your-active-profile
     *
     * You can find more information on how profiles work with JHipster on [https://www.jhipster.tech/profiles/]("https://www.jhipster.tech/profiles/").
     */
    @PostConstruct
    fun initApplication() {
        val activeProfiles = env.activeProfiles
        if (
            activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) &&
            activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)
        ) {
            log.error(
                "You have misconfigured your application! It should not run with both the 'dev' and 'prod' profiles at the same time."
            )
        }
        if (
            activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) &&
            activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_CLOUD)
        ) {
            log.error(
                "You have misconfigured your application! It should not run with both the 'dev' and 'cloud' profiles at the same time."
            )
        }
    }

    companion object {
        /**
         * Main method, used to run the application.
         *
         * @param args the command line arguments.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val env = runApplication<OrganizationApp>(*args) { DefaultProfileUtil.addDefaultProfile(this) }.environment
            logApplicationStartup(env)
        }

        @JvmStatic
        private fun logApplicationStartup(env: Environment) {
            val log = LoggerFactory.getLogger(OrganizationApp::class.java)

            val protocol = if (env.getProperty("server.ssl.key-store") != null) {
                "https"
            } else "http"
            val serverPort = env.getProperty("server.port")
            val contextPath = env.getProperty("server.servlet.context-path") ?: "/"
            var hostAddress = "localhost"
            try {
                hostAddress = InetAddress.getLocalHost().hostAddress
            } catch (e: UnknownHostException) {
                log.warn("The host name could not be determined, using `localhost` as fallback")
            }
            log.info(
                """

                ----------------------------------------------------------
                Application '${env.getProperty("spring.application.name")}' is running! Access URLs:
                Local:      $protocol://localhost:$serverPort$contextPath
                External:   $protocol://$hostAddress:$serverPort$contextPath
                Profile(s): ${env.activeProfiles.joinToString(",")}
                ----------------------------------------------------------
                """.trimIndent()
            )
        }
    }
}
