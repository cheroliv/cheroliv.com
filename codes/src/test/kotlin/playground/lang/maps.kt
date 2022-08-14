@file:Suppress("NonAsciiCharacters")
//(2)
package playground.lang

import kotlin.test.Test

class MapsTest {
    @Test fun main() {
        //map : La méthode map() crée un nouveau tableau
        // avec les résultats de l'appel d'une fonction fournie
        // sur chaque élément du tableau appelant.
        println(listDeNombresDe0a100.map { n -> n * 2 })
        println(listDeNombresDe0a100.map { it * 2 })

        println(listDeNombresDe0a100.average())
        val average = listDeNombresDe0a100.average()
        println(listDeNombresDe0a100.map { it - average })
        //en utilisant apply pour conserver le résulat établie avant apply
        listDeNombresDe0a100.average().toInt().apply average@{
            println(listDeNombresDe0a100.map { it - this@average })
        }
        //trie sans mise en plat: cad en conservant la structure de list de list
        println(listImbriqueDe0a100.map { it.sortedDescending() })

        //mise a plat en une list appres l'appel de map
        println(listImbriqueDe0a100.map { it.sortedDescending() }.flatten())

        //nombre d'éléments par list
        listImbriqueDe0a100.map { println(it.count()) }

        //flatMap: La méthode flatMap() permet d'appliquer une fonction
        // à chaque élément du tableau puis d'aplatir le résultat en un tableau.
        println(listImbriqueDe0a100.flatMap { it.sortedDescending() })

    }
}

val listImbriqueDe0a100
    get() = listOf(
        (0..33).toList(),
        (34..67).toList(),
        (68..100).toList()
    )