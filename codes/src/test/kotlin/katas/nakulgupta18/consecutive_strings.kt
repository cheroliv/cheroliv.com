package katas.nakulgupta18


import katas.nakulgupta18.LongestConsecLog.log
import org.slf4j.LoggerFactory.getLogger
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * consecutive_strings
 * @see <a href="https://www.codewars.com/kata/56a5d994ac971f1ac500003e/train/kotlin">nakulgupta18</a>
 *
 *
 *
 *
 * You are given an array(list) str_arr of strings and an integer k.
 * Your task is to return the first longest string,
 * consisting of k consecutive strings taken in the array.
 * Example:
 *
 * longest_consec(["zone", "abigail", "theta", "form", "libe",
 *            "zas", "theta", "abigail"], 2) --> "abigailtheta"
 *
 * n being the length of the string array, if n = 0 or k > n or k <= 0 return "".
 * Note
 *
 * consecutive strings : follow one after another without an interruption
 *
 */
data class Consec(val firstPos: Int, val motif: String)

fun longestConsec(strings: Array<String>, k: Int): String {
    @Suppress("UNUSED_VARIABLE") val length = strings.size
    var result = ""
    val lengthAtIndex = strings.map { it.length }
    val posByKConsec = mutableListOf<Int>()
    val firstPosByKConsec = mutableListOf<Int>()


    strings.forEachIndexed { idx, it ->
        var wc = 0
        strings.forEach { item -> if (it == item) wc++ }
        if (wc == k) posByKConsec.add(idx)
    }


    //posByKConsec: [1, 2, 3, 4, 8, 9]
    posByKConsec.forEachIndexed { i: Int, kPos: Int ->
        if (i == 0)
            firstPosByKConsec.add(kPos)
//        else
            //si le motif exist deja on ne prend pas
    }


//    var maxLength_ = 0
//    posByK.forEachIndexed { idx, it ->
//        val l = it.length
//        when {
//            maxLength_ < l -> maxLength_ = l
//        }
//    }

    log.info("posByLength: $lengthAtIndex")
    log.info("posByKConsec: $posByKConsec")
    log.info("firstPosByKConsec: $firstPosByKConsec")
//    log.info("max length: $maxLength_")
    return result


}

//fun longestConsec(strings: Array<String>, k: Int): String {
//    val length = strings.size
//    var result = ""
//    return when {
//        k <= 0 || length == 0 || k > length -> result
//        else -> {
//            strings.map { item ->
//                mapOf(item to mapOf(item to strings.filter { itemToCount ->
//                    itemToCount == item
//                }.size))
//            }.toList().filter {
//                k == it.entries.iterator().next().value.entries.iterator().next().value
//            }.forEach {
//                if (!result.contains(it.iterator().next().key))
//                    result += it.iterator().next().key
//            }
//
//
//            val maVar = strings.map { item ->
//                mapOf(item to mapOf(strings.filter { itemToCount ->
//                    itemToCount == item
//                }.size to item.length))
//            }
//            log.info(maVar.toString())
//
//
//            result
//        }
//    }
//}

class LongestConsecMainTest {
    companion object {
        private fun testing(actual: String, expected: String) {
            assertEquals(expected, actual)
        }
    }

    @Test
    fun preTest() {
//        val strings = arrayOf("zone", "abigail", "theta", "form", "libe", "zas", "theta", "abigail")
        val strings = arrayOf("zone", "abigai", "abigail", "abigai", "theta", "form", "libe", "zas", "theta", "abigail")
        log.info(longestConsec(strings, 2))//posByK: [1, 2, 3, 4, 8, 9]
    }

    //        @Test
    fun test() {
        println("longestConsec Fixed Tests")
        testing(longestConsec(arrayOf("zone", "abigail", "theta", "form", "libe", "zas", "theta", "abigail"), 2), "abigailtheta")
        testing(longestConsec(arrayOf("ejjjjmmtthh", "zxxuueeg", "aanlljrrrxx", "dqqqaaabbb", "oocccffuucccjjjkkkjyyyeehh"), 1), "oocccffuucccjjjkkkjyyyeehh")

    }
}

object LongestConsecLog {
    @JvmStatic
    val log = getLogger(LongestConsecLog.javaClass)
}

//fun longestConsec_(strarr: Array<String>): String {
//    return max(strarr.map { item ->
//        mapOf(item to mapOf(item to strarr.filter { itemToCount ->
//            itemToCount == item
//        }.size))
//    }.toList())
//}

//fun max(stringsCounted: List<Map<String, Map<String, Int>>>): String {
//    val occRates = mutableListOf<Int>()
//    stringsCounted.forEach {
//        occRates.add(it.entries.iterator().next().value.entries.iterator().next().value)
//    }
//    return stringsCounted[occRates.indexOf(occRates.maxByOrNull { it }!!)].keys.iterator().next()
//}
