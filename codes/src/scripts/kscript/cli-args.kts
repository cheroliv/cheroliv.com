#!/usr/bin/env kscript

val params = "toto titi tutu tata tete tonton toutou"

args.run {
    println("Arguments:")
    if (isNotEmpty())
        forEachIndexed { i: Int, arg: String -> println("arg #${i + 1}: $arg") }
    else println("no arg!")
}