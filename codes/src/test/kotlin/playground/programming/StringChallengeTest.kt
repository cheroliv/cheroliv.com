package playground.programming

import kotlin.test.Test

/**
 * str est composé de deux sequences de caracteres séparé par un espace
 * la séquence pattern
 *  +: un caractere de a - z
 *  $: un caractere de 1 - 9
 *  *: trois caracteres par defaut
 *      sauf si {N} N=nb de char pour cette etoile et N>=1
 */
// TODO à terminer
const val sample = "++*{5} gheeeee"

class StringChallengeTest {
    @Test
    fun test_stringChallenge() {
        println(stringChallenge(sample))
    }
}

fun stringChallenge(str: String): Boolean {
    if (str.whereIsWhiteSpace() == -1) return false

    val whiteSpaceIndex: Int = str.whereIsWhiteSpace()
    val pattern: String = str.extractPattern(whiteSpaceIndex)
    val motif: String = str.extractMotif(whiteSpaceIndex)

    println(str)
    println(pattern)
    println(motif)

    var indexPatternChecked = 0
    var indexMotifChecked = 0

    do {
        if ((pattern[indexPatternChecked] == '+' &&
                    motif[indexMotifChecked].isBelongToAzMinus()) ||
            pattern[indexPatternChecked] == '$' &&
            motif[indexMotifChecked].isBelongTo19()
        ) {
            indexPatternChecked++
            indexMotifChecked++
        } else return false

        if (pattern[indexPatternChecked] == '*') {
            pattern.evalStar(
                motif,
                Pair(indexPatternChecked, indexMotifChecked)
            ).run {
                repeat(first) {
                    if (motif[indexMotifChecked] == second)
                        indexMotifChecked++
                    else return false
                }
            }
        } else return false
    } while (indexMotifChecked <= motif.length)

    return true
}

fun String.evalStar(
    motif: String,
    indexes: Pair<Int, Int>
): Pair<Int, Char> =
    if (this[indexes.first] == '*' &&
        length >= indexes.first + 3 &&
        this[indexes.first + 1] == '{' &&
        this[indexes.first + 2].isBelongTo19() &&
        this[indexes.first + 3] == '}'
    ) Pair(this[indexes.first + 2].code, motif[indexes.second])
    else Pair(1, motif[indexes.second])


fun String.extractPattern(index: Int): String = substring(0, index)

fun String.extractMotif(index: Int): String = substring(index + 1, length - 1)

fun String.whereIsWhiteSpace(): Int {
    var result = -1
    forEachIndexed { i: Int, it: Char ->
        if (it == ' ') result = i
    }
    return result
}

fun Char.isBelongToAzMinus(): Boolean = (this == 'a') ||
        (this == 'b') ||
        (this == 'c') ||
        (this == 'd') ||
        (this == 'e') ||
        (this == 'f') ||
        (this == 'g') ||
        (this == 'h') ||
        (this == 'i') ||
        (this == 'j') ||
        (this == 'k') ||
        (this == 'l') ||
        (this == 'm') ||
        (this == 'n') ||
        (this == 'o') ||
        (this == 'p') ||
        (this == 'q') ||
        (this == 'r') ||
        (this == 's') ||
        (this == 't') ||
        (this == 'u') ||
        (this == 'v') ||
        (this == 'w') ||
        (this == 'x') ||
        (this == 'y') ||
        (this == 'z')

fun Char.isBelongTo19(): Boolean = (this == '1') ||
        (this == '2') ||
        (this == '3') ||
        (this == '4') ||
        (this == '5') ||
        (this == '6') ||
        (this == '7') ||
        (this == '8') ||
        (this == '9')