package organization.management

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Service

private const val INVALID_TOKENS_METER_NAME = "security.authentication.invalid-tokens"
private const val INVALID_TOKENS_METER_DESCRIPTION = "Indicates validation error count of the tokens presented by the clients."
private const val INVALID_TOKENS_METER_BASE_UNIT = "errors"
private const val INVALID_TOKENS_METER_CAUSE_DIMENSION = "cause"

@Service
class SecurityMetersService(private val registry: MeterRegistry) {

    val tokenInvalidSignatureCounter = invalidTokensCounterForCauseBuilder("invalid-signature").register(registry)
    val tokenExpiredCounter = invalidTokensCounterForCauseBuilder("expired").register(registry)
    val tokenUnsupportedCounter = invalidTokensCounterForCauseBuilder("unsupported").register(registry)
    val tokenMalformedCounter = invalidTokensCounterForCauseBuilder("malformed").register(registry)

    private fun invalidTokensCounterForCauseBuilder(cause: String): Counter.Builder {
        return Counter.builder(INVALID_TOKENS_METER_NAME)
            .baseUnit(INVALID_TOKENS_METER_BASE_UNIT)
            .description(INVALID_TOKENS_METER_DESCRIPTION)
            .tag(INVALID_TOKENS_METER_CAUSE_DIMENSION, cause)
    }

    fun trackTokenInvalidSignature() {
        tokenInvalidSignatureCounter.increment()
    }

    fun trackTokenExpired() {
        tokenExpiredCounter.increment()
    }

    fun trackTokenUnsupported() {
        tokenUnsupportedCounter.increment()
    }

    fun trackTokenMalformed() {
        tokenMalformedCounter.increment()
    }
}
