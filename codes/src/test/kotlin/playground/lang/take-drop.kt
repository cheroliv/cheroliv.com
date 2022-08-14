@file:Suppress("NonAsciiCharacters")
//(3)
package playground.lang

fun main() {
    // take
    // retourne la list contenant les premiers 10 éléments
    println(listDeNombresDe0a1000.take(10))
    // retourne la list sans les 900 premiers éléments
    println(listDeNombresDe0a1000.drop(900))
    // retourne la list contenant les premiers 10 éléments générés
    println(listDeNombreSautDeDixGenere.take(10).toList())
    // pas d'utilisation avec drop car la generation est infini
    // donc le dernier n'est pas accessible
}
val listDeNombreSautDeDixGenere: Sequence<Int> get() = generateSequence(0) { it + 10 }

val listDeNombresDe0a1000: List<Int> get() = (0..1000).toList()
