package playground.data

import playground.Data.nameToLoginNormalizer
import kotlinx.coroutines.*
import playground.Data
import kotlin.test.Test

class UsualDataTest {
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
        println(Data.intellectuals.nameToLoginNormalizer())
    }

}