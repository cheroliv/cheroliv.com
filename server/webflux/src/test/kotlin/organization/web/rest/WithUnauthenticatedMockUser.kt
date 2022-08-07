package organization.web.rest

import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContext
import org.springframework.security.test.context.support.WithSecurityContextFactory
import kotlin.annotation.Retention

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithUnauthenticatedMockUser.Factory::class)
annotation class WithUnauthenticatedMockUser {
    class Factory : WithSecurityContextFactory<WithUnauthenticatedMockUser?> {
        override fun createSecurityContext(annotation: WithUnauthenticatedMockUser?): SecurityContext {
            return SecurityContextHolder.createEmptyContext()
        }
    }
}
