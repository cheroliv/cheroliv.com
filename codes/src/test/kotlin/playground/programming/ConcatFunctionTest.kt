package playground.programming


import playground.data.Data.PI
import playground.data.Data.SEPARATOR
import playground.data.Data.programmingLanguages
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 * écriture d'un test unitaire, execution locale.
 * Manipulation d'éléments de langage et de quelques fonctions utiles.
 * (listof(), String.drop(), String.dropLast())
 */

class ConcatFunctionTest {
    fun concat(
        texts: Array<String>,
        separator: String = SEPARATOR
    ): String = texts.joinToString(separator)
    @Suppress("unused")
    fun List<String>.concat(): String = joinToString(SEPARATOR)

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testSomePrinties() {
        println("Hello World Android Basics")
        println("PI: $PI")
        println(programmingLanguages)
        println(concat(programmingLanguages))
    }

}