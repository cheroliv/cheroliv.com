package playground.programming

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * get_ascii_value_of_character
 * @see <a href="https://www.codewars.com/kata/56a5d994ac971f1ac500003e/train/kotlin">nakulgupta18</a>
 *
 */

val Char.ascii: Int get() = code.toByte().toInt()


class CharGetAsciiTest {
    @Test
    fun `simple tests`() {
        assertEquals(expected = 65, actual = 'A'.ascii)
        assertEquals(expected = 32, actual = ' '.ascii)
        assertEquals(expected = 33, actual = '!'.ascii)
    }
}
