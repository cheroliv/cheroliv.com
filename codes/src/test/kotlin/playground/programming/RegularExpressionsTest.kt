@file:Suppress(
    "NonAsciiCharacters",
    "ReplaceJavaStaticMethodWithKotlinAnalog",
    "DANGEROUS_CHARACTERS"
)

package playground.programming

import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors
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

    @Test
    fun `expressions régulières plus complexes`() {
        //Notez que nous devons utiliser \\ car nous avons besoin d'un littéral \
        //et Java utilise un seul \ comme caractère d'échappement
        var pStr = "\\d" // Un chiffre numérique

        var text = "Apollo 13"
        var p = Pattern.compile(pStr)
        var m = p.matcher(text)
        print(pStr + " matches " + text + "? " + m.find())
        println(" ; match: " + m.group())
        pStr = "[a..zA..Z]" //N'importe quelle lettre

        p = Pattern.compile(pStr)
        m = p.matcher(text)
        print(pStr + " matches " + text + "? " + m.find())
        println(" ; match: " + m.group())

        //N'importe quel nombre de lettres, qui doivent toutes être comprises entre 'a' et 'j'
        //mais peut-être en majuscule ou en minuscule.
        pStr = "([a..jA..J]*)"
        p = Pattern.compile(pStr)
        m = p.matcher(text)
        print(pStr + " matches " + text + "? " + m.find())
        println(" ; match: " + m.group())
        text = "abacab"
        //'a' suivi de quatre caractères quelconques, suivi de 'b'
        pStr = "a....b"
        p = Pattern.compile(pStr)
        m = p.matcher(text)
        print(pStr + " matches " + text + "? " + m.find())
        println(" ; match: " + m.group())
    }

    @Test
    fun `Quelles chaînes correspondent à la regex ?`() {
        val pStr = "\\d" // Un chiffre numérique
        val p = Pattern.compile(pStr)
        val ls: List<String> = Arrays.asList("Cat", "Dog", "Ice-9", "99 Luftballoons")
        val containDigits: List<String> = ls.stream()
            .filter(p.asPredicate())
            .collect(Collectors.toList())
        assert(containDigits.contains("Ice-9"))
        assert(containDigits.contains("99 Luftballoons"))
        assertEquals(2, containDigits.size)
    }
}