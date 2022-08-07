package organization.repository.rowmapper

import io.r2dbc.spi.Row
import org.springframework.stereotype.Service
import organization.domain.User
import java.time.Instant
import java.util.function.BiFunction

/**
 * Converter between [Row] to [User], with proper type conversions.
 */
@Service
class UserRowMapper(
    private val converter: ColumnConverter
) : BiFunction<Row, String, User> {
    /**
     * Take a [Row] and a column prefix, and extract all the fields.
     * @return the [User] stored in the database.
     */
    override fun apply(row: Row, prefix: String): User {
        val entity = User()
        entity.id = converter.fromRow(row, "${prefix}_id", Long::class.java)
        entity.login = converter.fromRow(row, "${prefix}_login", String::class.java)
        entity.password = converter.fromRow(row, "${prefix}_password", String::class.java)
        entity.firstName = converter.fromRow(row, "${prefix}_first_name", String::class.java)
        entity.lastName = converter.fromRow(row, "${prefix}_last_name", String::class.java)
        entity.email = converter.fromRow(row, "${prefix}_email", String::class.java)
        entity.activated = converter.fromRow(row, "${prefix}_activated", Boolean::class.java) == true
        entity.langKey = converter.fromRow(row, "${prefix}_lang_key", String::class.java)
        entity.imageUrl = converter.fromRow(row, "${prefix}_image_url", String::class.java)
        entity.activationKey = converter.fromRow(row, "${prefix}_activation_key", String::class.java)
        entity.resetKey = converter.fromRow(row, "${prefix}_reset_key", String::class.java)
        entity.resetDate = converter.fromRow(row, "${prefix}_reset_date", Instant::class.java)
        return entity
    }
}
