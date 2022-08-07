package organization.config

import liquibase.integration.spring.SpringLiquibase
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import tech.jhipster.config.JHipsterConstants
import tech.jhipster.config.liquibase.AsyncSpringLiquibase
import java.util.concurrent.Executor
import javax.sql.DataSource

@Configuration
class LiquibaseConfiguration(private val env: Environment) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun liquibase(
        @Qualifier("taskExecutor") executor: Executor,
        liquibaseProperties: LiquibaseProperties,
        dataSourceProperties: R2dbcProperties
    ) =
        createAsyncSpringLiquibase(this.env, executor, liquibaseProperties, dataSourceProperties)
            .apply {
                changeLog = "classpath:config/liquibase/master.xml"
                contexts = liquibaseProperties.contexts
                defaultSchema = liquibaseProperties.defaultSchema
                liquibaseSchema = liquibaseProperties.liquibaseSchema
                liquibaseTablespace = liquibaseProperties.liquibaseTablespace
                databaseChangeLogLockTable = liquibaseProperties.databaseChangeLogLockTable
                databaseChangeLogTable = liquibaseProperties.databaseChangeLogTable
                isDropFirst = liquibaseProperties.isDropFirst
                labels = liquibaseProperties.labels
                setChangeLogParameters(liquibaseProperties.parameters)
                setRollbackFile(liquibaseProperties.rollbackFile)
                isTestRollbackOnUpdate = liquibaseProperties.isTestRollbackOnUpdate

                if (env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_NO_LIQUIBASE))) {
                    setShouldRun(false)
                } else {
                    setShouldRun(liquibaseProperties.isEnabled)
                    log.debug("Configuring Liquibase")
                }
            }

    fun createAsyncSpringLiquibase(
        env: Environment,
        executor: Executor,
        liquibaseProperties: LiquibaseProperties,
        dataSourceProperties: R2dbcProperties
    ): SpringLiquibase {
        val liquibase = AsyncSpringLiquibase(executor, env)
        liquibase.setDataSource(createNewDataSource(liquibaseProperties, dataSourceProperties))
        return liquibase
    }

    fun createNewDataSource(liquibaseProperties: LiquibaseProperties, dataSourceProperties: R2dbcProperties): DataSource {
        val user = getProperty(liquibaseProperties.user, dataSourceProperties.getUsername())
        val password = getProperty(liquibaseProperties.password, dataSourceProperties.getPassword())
        return DataSourceBuilder.create()
            .url(liquibaseProperties.url)
            .username(user)
            .password(password)
            .build()
    }

    fun getProperty(property: String?, defaultValue: String) = property ?: defaultValue
}
