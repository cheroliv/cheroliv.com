@file:Suppress("NonAsciiCharacters")
//(5)
package playground.lang

import kotlin.test.Test

/**
 * Défi: utilisation des fonctions d'ordre supérieur dans Kotlin
 *
 * Dans ce défi, j'appliquerez des fonctions d'ordre supérieur
 * de la bibliothèque standard de Kotlin,
 * et des concepts de programmation fonctionnelle pour effectuer
 * une analyse simple des données.
 *
 */
val mapAgeDesUsers
    get() = mapOf(
        "users1.csv" to listOf(32, 45, 17, -1, 34),
        "users2.csv" to listOf(19, -1, 67, 22),
        "users3.csv" to listOf(),
        "users4.csv" to listOf(56, 32, 18, 44)
    )
class ChainingTest{
   @Test fun main() {
        // 1) Age moyen des Users
        //      a) mise a plat des listes en une seule (a)
        //      b) filtrer de cette liste
        //          les elements sup ou égal à zéro dans une list (b)
        //      c) Le résultat est la moyenne de la liste (b)
        println("L'Age moyen des users: %.2f."
            .format(
                mapAgeDesUsers
                .flatMap { it.value }
                .filter { it >= 0 }
                .average()))

        // 2) Fichiers avec des données incorrectes
        println("Fichiers avec des données incorrectes: ${
            mapAgeDesUsers
                .filter { it.value.any { it < 0 } }
                .map { it.key }
        }.")

        // 3) Nombre d'entrées incorrectes
        println(
            "Il y a ${
                mapAgeDesUsers
                    .flatMap { it.value }
                    .filter { it < 0 }
                    .count()
            } données incorrectes.")
    }
}