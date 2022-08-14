package playground.math

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals


fun computeClosestToZero(ts: Array<Int>): Int = when {
    ts.isEmpty() -> 0
    else -> ts.minOf { abs(it) }
}

@Suppress("NonAsciiCharacters")
class ComputeClosestToZeroTest {
    private val temperatures by lazy { arrayOf(-12, -15, -2, 1, 17) }


    @Test
    fun `test computeClosestToZero sur températures doit etre 1`() {
        assertEquals(computeClosestToZero(temperatures), 1)
    }
}
