package playground

import org.apache.commons.lang3.StringUtils
import java.text.Normalizer

object Data {

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

    fun CharSequence.unaccent(): String =
        "\\p{InCombiningDiacriticalMarks}+"
            .toRegex()
            .replace(
                Normalizer.normalize(this, Normalizer.Form.NFD),
                ""
            )

    fun Array<String>.nameToLogin(): Array<String> =
        map {
            StringUtils.stripAccents(
                it.lowercase().replace(
                    oldChar = ' ',
                    newChar = '.'
                )
            )
        }.toTypedArray()

    fun Array<String>.nameToLoginNormalizer(): Array<String> = map {
        it.lowercase()
            .replace(' ', '.')
            .unaccent()
    }.toTypedArray()
}