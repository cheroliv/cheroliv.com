#!/usr/bin/env groovy
println System.getProperty("user.home")

static String getSeparator_() {
    System.getProperty("file.separator")
}

String separator = separator_

String path = System.getProperty("user.home")
        .concat("${separator}.gradle" +
                separator +
                "gradle.properties")

File propertiesFile = new File(path)

assert propertiesFile.exists() && !propertiesFile.directory

Properties gradleProperties = new Properties()

propertiesFile.withInputStream { InputStream it ->
    gradleProperties.load(it)
}

String user_name = gradleProperties.getProperty("user_name")

String text
String resultText
text = new File("${System.getProperty("user.home")}" +
        separator +
        "src" +
        separator +
        "cheroliv.github.io" +
        separator +
        "src" +
        separator +
        "main" +
        separator +
        "resources" +
        separator +
        "eco_space.txt").text

String to = separator +
        "media" +
        separator +
        "${user_name}" +
        separator +
        "320" +
        separator +
        "videoCours" +
        separator
String from = "${System.getProperty("user.home")}" +
        separator +
        "p2p" +
        separator

resultText = text.replace(to, from)

println resultText

//execute command line in a string
println "rsync -avr ${resultText} ${to}".execute().text
