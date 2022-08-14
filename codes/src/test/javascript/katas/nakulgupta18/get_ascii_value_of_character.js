// Test.assertEquals(getASCII('A'),65)
// Test.assertEquals(getASCII(' '),32)
// Test.assertEquals(getASCII('!'),33)

const getASCII = c => c.toString().charCodeAt(0)

const c = 'A'
console.log(`getASCII(${c}): ${getASCII(c)}`)