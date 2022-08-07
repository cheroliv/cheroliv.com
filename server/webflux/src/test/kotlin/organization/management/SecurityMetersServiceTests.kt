package organization.management

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private const val INVALID_TOKENS_METER_EXPECTED_NAME = "security.authentication.invalid-tokens"

class SecurityMetersServiceTests {

    private lateinit var meterRegistry: MeterRegistry

    private lateinit var securityMetersService: SecurityMetersService

    @BeforeEach
    fun setup() {
        meterRegistry = SimpleMeterRegistry()

        securityMetersService = SecurityMetersService(meterRegistry)
    }

    @Test
    fun testInvalidTokensCountersByCauseAreCreated() {
        meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME).counter()

        meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME)
            .tag("cause", "expired")
            .counter()

        meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME)
            .tag("cause", "unsupported")
            .counter()

        meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME)
            .tag("cause", "invalid-signature")
            .counter()

        meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME)
            .tag("cause", "malformed")
            .counter()

        val counters = meterRegistry.find(INVALID_TOKENS_METER_EXPECTED_NAME).counters()

        assertThat(counters).hasSize(4)
    }

    @Test
    fun testCountMethodsShouldBeBoundToCorrectCounters() {
        assertThat(
            meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME)
                .tag("cause", "expired")
                .counter().count()
        ).isZero()

        securityMetersService.trackTokenExpired()

        assertThat(
            meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME)
                .tag("cause", "expired")
                .counter().count()
        ).isEqualTo(1.0)

        assertThat(
            meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME)
                .tag("cause", "unsupported")
                .counter().count()
        ).isZero()

        securityMetersService.trackTokenUnsupported()

        assertThat(
            meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME)
                .tag("cause", "unsupported")
                .counter().count()
        ).isEqualTo(1.0)

        assertThat(
            meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME)
                .tag("cause", "invalid-signature")
                .counter().count()
        ).isZero()

        securityMetersService.trackTokenInvalidSignature()

        assertThat(
            meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME)
                .tag("cause", "invalid-signature")
                .counter().count()
        ).isEqualTo(1.0)

        assertThat(
            meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME)
                .tag("cause", "malformed")
                .counter().count()
        ).isZero()

        securityMetersService.trackTokenMalformed()

        assertThat(
            meterRegistry.get(INVALID_TOKENS_METER_EXPECTED_NAME)
                .tag("cause", "malformed")
                .counter().count()
        ).isEqualTo(1.0)
    }
}
