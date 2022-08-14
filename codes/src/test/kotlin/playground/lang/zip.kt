@file:Suppress("NonAsciiCharacters")
//(4)
package playground.lang

import playground.Data
import kotlin.test.Test

class ZipTest {
    @Test fun main() {
        // zip:
        // Renvoie une liste de paires construites à partir des éléments
        // de ce tableau et de l'autre tableau avec le même index.
        // La liste renvoyée a la longueur de la collection la plus courte.

        // list des Pair<String,Boolean> associant les elements par occurence
        println(Data.basics.zip(someBooleans))

        //list des résultats du texte et de la condition contains() rapproché par pair
        println(Data.basics.zip(Data.basics.map { it.contains("t") }))
    }
}

val someBooleans = arrayOf(
        false,
        true,
        true,
        false
    )
