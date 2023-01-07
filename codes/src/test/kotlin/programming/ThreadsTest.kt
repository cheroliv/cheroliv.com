@file:Suppress("UsePropertyAccessSyntax")

package programming

import java.text.DateFormat
import java.util.*
import kotlin.test.Test

class ThreadsTest {

    @Test
    fun `threads test`() {
        //java.lang.Thread représente le thread fondamentale de l'API java
        //il existe deux manière de définir un thread
        //1) étendre la classe Thread ou une lambda en kotlin
        //2) implémenter l'interface Runnable,
        //      puis passer une instance de cet objet Runnable au constructeur de Thread.

        val list1: List<Int> = List(
            size = 45,
            init = { (1..31).random() }
        )
        println("list1$list1")
        //facon 1
        val t = Thread {
            Collections.sort(list1)
            println("list1 sorted$list1")
        }
        t.start()

        val list2 = List(
            size = 45,
            init = { (1..31).random() }
        )
        println("list2$list2")
        //facon 2
        val sorter = BackgroundSorter(list2)
        sorter.start()

        //priorité des threads
        //tant qu'un thread de niveau de priorité supérieure n'est pas fini
        //alors celui de niveau inférieur ne peut s'exécuter

        //on définit un thread avec une priorité inférieur à la normale
        t.setPriority(Thread.NORM_PRIORITY - 1)

        //ici on définit un thread avec une priorité inférieur à la priorité
        //du thread courant
        t.setPriority(Thread.currentThread().getPriority() - 1)

        //Thread.yield() fait une pause pour laisser les autres threads de meme priorité s'exécuter
    }

    class BackgroundSorter(private val l: List<Int>) : Thread() {
        override fun run() {
            Collections.sort(l)
            println("list2 sorted$l")
        }
    }

    //pour l'arrêt du thread, plutôt qu'utiliser la fonction Thread.stop()
    // qui laisse la memoire dans un état non controlé.
    //utiliser la méthode tel que l'exemple pleaseStop()
    class DummyClock(
        private val df: DateFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM),
        private var keepRunning: Boolean = true
    ) : Thread() {
        init {
            isDaemon = true
            println("init")
            start()
        }

        override fun run() {
            println("run in! $keepRunning")
            while (keepRunning) {
                println(df.format(Date()))
                try {
                    sleep(1000)
                } catch (e: InterruptedException) {
                    println(e.message)
                }
            }
            println("run out: $keepRunning")
        }

        fun pleaseStop() {
            keepRunning = false
        }
    }

    //java.util.Timer
    //java.util.TimerTask
    //ces classes permettent l'exécution de taches répétitives
    @Test
    fun `Timer et TimerTask test`() {
        DummyClock()

    }
}