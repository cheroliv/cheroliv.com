package playground.programming

import kotlin.test.Test

class BirthdayMessageTestOutput {
    /**
     * variable et affichage
     */
    @Test
    fun simple_test() {
        println("Create a birthday message in Kotlin")
        println("Happy Birthday, Rover!")
        println("You are already 5!")
        println("5 is the very best age to celebrate!")

        println("   ,,,,,   ")
        println("   |||||   ")
        println(" =========")
        println("@@@@@@@@@@@")
        println("{~@~@~@~@~}")
        println("@@@@@@@@@@@")
        println()

        println("=======================")
        println("Happy Birthday, Jhansi!")
        println("=======================")
        println()

        printBorderOpiniatedPrimitif()
        println("Happy Birthday, Pierre!")
        printBorderOpiniatedPrimitif()
        println()

        printBorderOpiniated()
        println("Happy Birthday, Paul!")
        printBorderOpiniated()
        println()


        val border0 = "%"


        printBorderSettableMotif(border0)
        println("Happy Birthday, Jacques!")
        printBorderSettableMotif(border0)
        println()


        val border = "`-._,-'"
        val timesToRepeat = 4
        printBorderSettableMotif(border, timesToRepeat)
        println("\tHappy Birthday, Foo!")
        printBorderSettableMotif(border, timesToRepeat)
        println()


        val age = 24
        val layers = 5
        printCake(age, layers)
    }


    /**
     * fonction
     */
    fun printBorderOpiniatedPrimitif() {
        println("=======================")
    }

    /**
     * itération
     */
    fun printBorderOpiniated() =
        repeat(times = 23) {
            print("=")
        }.run { println() }

    fun printBorderSettableMotif(motif: String) {
        repeat(times = 23) {
            print(motif)
        }
        println()
    }

    fun printBorderSettableMotif(border: String, timesToRepeat: Int) {
        repeat(timesToRepeat) {
            print(border)
        }
        println()
    }

    fun printCake(
        age: Int,
        layers: Int
    ) {
        printCakeCandles(age)
        printCakeTop(age)
        printCakeBottom(age, layers)
    }

    fun printCakeCandles(age: Int) {
        print(" ")
        repeat(age) {
            print(",")
        }
        println() // Print an empty line

        print(" ") // Print the inset of the candles on the cake
        repeat(age) {
            print("|")
        }
        println()
    }

    fun printCakeTop(age: Int) =
        repeat(times = age + 2) {
            print("=")
        }.run { println() }

    fun printCakeBottom(
        age: Int,
        layers: Int
    ): Unit =
        repeat(layers) {
            repeat(age + 2) {
                print("@")
            }
            println()
        }
}
