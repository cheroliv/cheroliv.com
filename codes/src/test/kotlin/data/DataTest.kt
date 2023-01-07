package data

import kotlinx.coroutines.*
import kotlin.test.Test


object DataTest {
    internal class UsualDataTest {
        @OptIn(DelicateCoroutinesApi::class)
        @Test
        fun data_test() {
            val result: List<Deferred<Int>> = (1..1_000).map { n ->
                val deferred: Deferred<Int> = GlobalScope.async {
                    delay(1_000)
                    println(n)
                    n
                }
                deferred
            }
            runBlocking {
                println("the sum is ${result.sumOf { it.await() }}")
            }
            println(intellectuals.nameToLoginNormalizer())
        }

    }

    val basics: Array<String> = arrayOf(
        "foo",
        "bar",
        "baz",
        "qux",
        "quux",
        "corge",
        "grault",
        "garply",
        "waldo",
        "fred",
        "plugh",
        "xyzzy",
        "thud"
    )

    @Suppress("MemberVisibilityCanBePrivate")
    var categories: Array<String> = arrayOf(
        "boissons",
        "gateaux",
        "fruits",
        "légumes"
    )
    var articles: Array<Array<String>> = arrayOf(
        arrayOf("coca-cola", "pepsi", "orangina"),
        arrayOf("pepito", "granola", "makrout"),
        arrayOf("orange", "raisin", "pomme"),
        arrayOf("carrote", "tomate", "oignon")
    )
    const val PI: Double = 3.1415
    const val SEPARATOR = ", "
    val programmingLanguages: Array<String> = arrayOf(
        "Android",
        "Kotlin",
        "Java",
        "Javascript",
        "Typescript",
        "Groovy",
        "Go",
        "Swift",
        "iOS"
    )

    @JvmStatic
    val intellectuals: Array<String> = arrayOf(
        "Karl Marx",
        "Jean-Jacques Rousseau",
        "Victor Hugo",
        "Platon",
        "René Descartes",
        "Socrate",
        "Homère",
        "Paul Verlaine",
        "Claude Roy",
        "Bernard Friot",
        "François Bégaudeau",
        "Frederic Lordon",
        "Antonio Gramsci",
        "Georg Lukacs",
        "Franz Kafka",
        "Arthur Rimbaud",
        "Gérard de Nerval",
        "Paul Verlaine",
        "Dominique Pagani",
        "Rocé",
        "Chrétien de Troyes",
        "François Rabelais",
        "Montesquieu",
        "Georg Hegel",
        "Friedrich Engels",
        "Voltaire",
        "Michel Clouscard"
    )
}