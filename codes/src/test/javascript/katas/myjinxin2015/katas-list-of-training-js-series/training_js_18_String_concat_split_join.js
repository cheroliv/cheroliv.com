// Test.assertSimilar(splitAndMerge("My name is John"," ") , "M y n a m e i s J o h n");
// Test.assertSimilar(splitAndMerge("My name is John","-") , "M-y n-a-m-e i-s J-o-h-n");
// Test.assertSimilar(splitAndMerge("Hello World!",".") , "H.e.l.l.o W.o.r.l.d.!");
// Test.assertSimilar(splitAndMerge("Hello World!",",") , "H,e,l,l,o W,o,r,l,d,!");

const splitAndMerge = (string, separator) => {
    let result = []
    string.split(" ").forEach(word => {
        result.push(word.split("").join(separator))
    })
    return result.join(" ")
}

const result = splitAndMerge("Hello World!", ".")
console.log(`result: ${result}`)
