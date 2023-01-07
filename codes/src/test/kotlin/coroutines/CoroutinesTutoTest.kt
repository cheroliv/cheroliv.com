package coroutines

import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.test.Test

/**
 * to run the project
 * ```
 * ./gradlew check
 * ```
 */
class CoroutinesTutoTest {

    companion object {
        @JvmStatic
        val log: Logger = LoggerFactory.getLogger(CoroutinesTutoTest::class.java)
    }

    @Test
    fun testFirstCoroutine() = firstCoroutine()

    //page 53
    fun firstCoroutine() {
        (1..500).forEach {
            GlobalScope.launch {
                val threadName = Thread.currentThread().name
                log.info("$it printed on thread $threadName")
            }
        }
        Thread.sleep(1000)
    }

    @Test
    fun testSecondCoroutine() = secondCoroutine()

    //page 58
    fun secondCoroutine() {
        GlobalScope.launch {
            log.info("Hello coroutine!")
            delay(500)
            log.info("Right back at ya!")
        }
        Thread.sleep(1000)
    }

    @Test
    fun testThirdCoroutine() = thirdCoroutine()

    //page 59
    fun thirdCoroutine() {
        val job1 = GlobalScope.launch(start = CoroutineStart.LAZY) {
            delay(200)
            log.info("Pong")
            delay(200)
        }

        GlobalScope.launch {
            delay(200)
            log.info("Ping")
            job1.join()
            log.info("Ping")
            delay(200)
        }
        Thread.sleep(1000)
    }

    @Test
    fun testFourthCoroutine() = fourthCoroutine()


    //page 60
    fun fourthCoroutine() {
        with(GlobalScope) {
            val parentJob = launch {
                delay(200)
                log.info("I’m the parent")
                delay(200)
            }
            launch(context = parentJob) {
                delay(200)
                log.info("I’m a child")
                delay(200)
            }
            if (parentJob.children.iterator().hasNext())
                log.info("The Job has children ${parentJob.children}")
            else log.info("The Job has NO children")
            Thread.sleep(1000)
        }
    }

    @Test
    fun testFifthCoroutine() = fifthCoroutine()

    //page 61
    @Suppress("UNUSED_VALUE")
    fun fifthCoroutine() {
        var isDoorOpen = false
        log.info("Unlocking the door... please wait.\n")
        GlobalScope.launch { delay(3000) }
        isDoorOpen = true
        GlobalScope.launch {
            repeat(4) {
                log.info("Trying to open the door...\n")
                delay(800)
            }
        }
        if (isDoorOpen)
            log.info("Opened the door!\n")
        else log.info("The door is still locked\n")
        Thread.sleep(5000)
    }

    @Test
    fun testSixthUICoroutine() = sixthUICoroutine()

    //page 62
    fun sixthUICoroutine() {
        GlobalScope.launch {
            val bgThreadName = Thread.currentThread().name
            log.info("I’m Job 1 in thread $bgThreadName")
            delay(200)
            GlobalScope.launch(Dispatchers.Main) {
                val uiThreadName = Thread.currentThread().name
                log.info("I’m Job 2 in thread $uiThreadName")
            }
        }
        Thread.sleep(1000)
    }
}