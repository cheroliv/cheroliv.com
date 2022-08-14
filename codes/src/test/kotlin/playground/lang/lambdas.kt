//(1)
package playground.lang

import kotlin.test.Test

//HOF: high order function, fonction d'ordre supérieure
val listDeNombresDe0a100: List<Int> get() = (0..100).toList()
fun estCePair(i: Int) = i % 2 == 0
fun estCeImpair(i: Int) = i % 2 != 0
fun isThatEven(i: Int) = i % 2 == 0
fun isThatOdd(i: Int) = i % 2 != 0

//extension function, fonction d'extension (de la classe)
fun Int.estIlPair() = this % 2 == 0
fun Int.estIlImpair() = this % 2 != 0
fun Int.isEven() = this % 2 == 0
fun Int.isOdd() = this % 2 != 0

class LambdaTest {
    @Test fun main() {
        //lambda
        val foisDeux: (Int) -> Int = { x: Int -> x * 2 }
        val addition: (Int, Int) -> Int = { x: Int, y: Int -> x + y }

        //fonction anonyme
        // (callback), affecté à une variable
        // fonction expression body style
        @Suppress("UNUSED_VARIABLE") val foisDeuxFunAno: (Int) -> Int = fun(x: Int): Int = x * 2
        //fonction block body style
        @Suppress("UNUSED_VARIABLE") val additionFunAno: (Int, Int) -> Int = fun(x: Int, y: Int): Int {
            return x + y
        }

        println(foisDeux(2))
        println(addition(3, 7))
        //filter: La méthode filter() crée et retourne un nouveau tableau
        // contenant tous les éléments du tableau d'origine qui remplissent
        // une condition déterminée par la fonction callback
        // (une lambda ou une fonction anonyme)
        println(listDeNombresDe0a100.filter { nombre -> nombre % 2 == 0 })
        println(listDeNombresDe0a100.filter { it % 2 == 0 })
        println(listDeNombresDe0a100.filter { it.estIlPair() })
        println(listDeNombresDe0a100.filter { it.estIlImpair() })
        println(listDeNombresDe0a100.filter(::estCePair))
        println(listDeNombresDe0a100.filter(::estCeImpair))
    }
}