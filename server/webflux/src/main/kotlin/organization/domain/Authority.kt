package organization.domain

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * An authority (a security role) used by Spring Security.
 */
@Table("jhi_authority")
data class Authority(

    @field:NotNull
    @field:Size(max = 50)
    @Id
    var name: String? = null

) : Serializable, Persistable<String> {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Authority) return false
        if (other.name == null || name == null) return false

        return name == other.name
    }

    override fun hashCode() = 31

    override fun getId() = name

    override fun isNew() = true

    companion object {
        private const val serialVersionUID = 1L
    }
}
