package organization

import org.apache.commons.lang3.StringUtils
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.util.Collections
import java.util.concurrent.atomic.AtomicBoolean

class ReactiveSqlTestContainerExtension : BeforeAllCallback {

    private val started: AtomicBoolean = AtomicBoolean(false)

    companion object {

        private val container: PostgreSQLContainer<*> = PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:14.2"))
            .apply {
                withDatabaseName("organization")
                withTmpFs(Collections.singletonMap("/testtmpfs", "rw"))
            }
    }

    @Throws(Exception::class)
    override fun beforeAll(extensionContext: ExtensionContext) {
        if (!started.get() && useTestcontainers()) {
            container.start()
            System.setProperty("spring.r2dbc.url", container.jdbcUrl.replace("jdbc", "r2dbc"))
            System.setProperty("spring.r2dbc.username", container.username)
            System.setProperty("spring.r2dbc.password", container.password)
            System.setProperty("spring.liquibase.url", container.jdbcUrl)
            System.setProperty("spring.liquibase.user", container.username)
            System.setProperty("spring.liquibase.password", container.password)
            started.set(true)
        }
    }

    private fun useTestcontainers(): Boolean {

        val systemProperties = StringUtils.defaultIfBlank(System.getProperty("spring.profiles.active"), "")
        val environmentVariables = StringUtils.defaultIfBlank(System.getenv("SPRING_PROFILES_ACTIVE"), "")

        return systemProperties.contains("testcontainers") || environmentVariables.contains("testcontainers")
    }
}
