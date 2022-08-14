#!/usr/bin/env groovy
import java.nio.charset.StandardCharsets

List<Character> chars = new ArrayList<>()
int j = 0
Character jumpLine = '\n'

256.times { Integer idx ->
    if (Character.isAlphabetic(idx) || Character.isDigit(idx)) {
        if (j % 10 == 0 && j != 0) {
            chars.add(jumpLine)
            chars.add(idx as char)
        } else chars.add(idx as char)
        j++
    }
}

String seperator = System.getProperty("file.separator")

String path = "${System.getProperty("user.home")}${seperator}ascii.txt"
File speCharFile = new File(path)

if (speCharFile.exists() && !speCharFile.directory) {
    speCharFile.text = ""
} else {
    speCharFile.createNewFile()
}

String text = new String()

chars.each { Character it ->
    text = it == jumpLine ? "$text$it" : "$text $it "
}

speCharFile.setText(text, StandardCharsets.UTF_8.toString())
