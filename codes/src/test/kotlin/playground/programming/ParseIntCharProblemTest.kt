@file:Suppress("MemberVisibilityCanBePrivate")

package playground.programming

import kotlin.test.Test
import kotlin.test.assertEquals

class ParseIntCharProblemTest {

    fun getAge(y: String): Int = y.filter {
        it.isDigit()
    }.toInt()

    @Test
    fun `deviner l'age contenu au debut de la chaine de caractere`() {
        assertEquals(52, getAge("52 kkkkkkk"))
    }
}
