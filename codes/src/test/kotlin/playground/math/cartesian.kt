package playground.math

import kotlin.random.Random.Default.nextInt




data class Point(
    val x: Number,
    val y: Number,
    val z: Number = 0
)

data class Droite(
    val m: Number,
    val p: Number,
    val x: Number = 0
) {
    private val y: Number
        get() = m.toDouble() * x.toDouble() + p.toDouble()
    val message: String
        get() = "Le point ${Point(x, y)} appartient à la droite y=${m}x+$p"
}

fun main() {
    Droite(
        m = nextInt(2, 6),
        p = nextInt(15),
        x = nextInt()
    ).run { println(message) }
}