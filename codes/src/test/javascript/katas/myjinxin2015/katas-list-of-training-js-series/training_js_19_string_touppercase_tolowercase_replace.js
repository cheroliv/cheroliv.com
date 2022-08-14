// Test.assertSimilar(alienLanguage("My name is John") , "My NAMe Is JOHn");
// Test.assertSimilar(alienLanguage("this is an example") , "THIs Is An EXAMPLe");
// Test.assertSimilar(alienLanguage("Hello World") , "HELLo WORLd");
// Test.assertSimilar(alienLanguage("HELLO WORLD") , "HELLo WORLd");


const alienLanguage = humanSentence => {
    String.prototype.replaceCharAt = function (index, replacement) {
        return this.substr(0, index)
            + replacement
            + this.substr(index + replacement.length);
    }

    String.prototype.reverse = function (str) {
        return str ? reverse(str.substr(1)) + str[0] : str
    }

    let alienSentence = []
    humanSentence.split(" ").forEach(word => {
        const OUT_OF_RANGE = -1;
        let lastIndex = OUT_OF_RANGE
        for (let i = word.length - 1; i >= 0; i--) {
            const char_ = word[i].toLowerCase().charCodeAt(0)
            if (char_ >= "a".charCodeAt(0)
                && char_ <= "z".charCodeAt(0)
                && lastIndex === OUT_OF_RANGE)
                lastIndex = word.length - 1 - i
        }
        if (lastIndex === OUT_OF_RANGE) {
            alienSentence.push(word.toUpperCase().replaceCharAt(
                word.length - 1,
                word[word.length - 1].toLowerCase()))
        } else {
            alienSentence.push(word.toUpperCase().replaceCharAt(
                word.length - 1 - lastIndex,
                word[word.length - 1 - lastIndex].toLowerCase()))
        }
    })
    return alienSentence.join(" ")
}
console.log(`alienLanguage: ${alienLanguage("HELLO WORLD")}`)