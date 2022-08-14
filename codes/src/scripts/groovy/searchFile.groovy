#!/usr/bin/env groovy
String motif = 'Python pour les pentesteurs 2-2'
File home = new File(System.getProperty('user.home'))
assert home.isDirectory()
println motif
home.eachDirRecurse { File it ->
    if (it.name.contains(motif)) println it.canonicalPath
}