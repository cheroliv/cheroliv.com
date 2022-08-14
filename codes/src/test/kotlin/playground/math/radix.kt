package playground.math

import kotlin.test.Test


fun displayDecimalNumberBaseInNBase(x: Int, n: Int) {
    println("$x en base $n = ${x.toString(n)}")
}

class RadixTests {
    @Test
    fun `main test`() {
        2.apply {
            listOf(0, 1, 2, 3, 4, 5).map {
                displayDecimalNumberBaseInNBase(it, this)

            }
            assert(0.toString(radix = this) == "0")
            assert(1.toString(this) == "1")
            assert(2.toString(this) == "10")
            assert(3.toString(this) == "11")
            assert(4.toString(this) == "100")
            assert(5.toString(this) == "101")
        }
    }
}