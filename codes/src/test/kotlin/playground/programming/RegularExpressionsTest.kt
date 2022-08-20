@file:Suppress("NonAsciiCharacters")

package playground.programming

import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.test.Test
import kotlin.test.assertEquals

class RegularExpressionsTest {
    @Test
    fun `expressions régulières`() {
        val p: Pattern = Pattern.compile("honou?r")
        val caesarUK = "For Brutus is an honourable man"
        val mUK: Matcher = p.matcher(caesarUK)
        assertEquals(true, mUK.find(), "Should matches UK spelling")
        val caesarUS = "For Brutus is an honorable man"
        val mUS: Matcher = p.matcher(caesarUS)
        assertEquals(true, mUS.find(), "Should matches US spelling")
    }
}