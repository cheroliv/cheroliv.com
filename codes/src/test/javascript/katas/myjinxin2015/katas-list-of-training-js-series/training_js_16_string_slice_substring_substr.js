//https://github.com/myjinxin2015/Katas-list-of-Training-JS-series
//Training JS series - for javascript beginner - myjinxin2015
// https://www.codewars.com/collections/training-js-series-for-javascript-beginner-myjinxin2015

// Kata 16
//https://www.codewars.com/kata/57274562c8dcebe77e001012/solutions/javascript

// data
const items = ["codewars", "javascript", "java", "kotlin", "groovy"]
console.log(items)

// my solution, simplest:
const cutIt = arr => {
    if (arr == null || arr.size === 0) return []
    let {length: min} = arr[0]
    arr.forEach(({length}) => {
        if (length < min) min = length;
    })
    let result = []
    arr.forEach(value => result.push(value.substr(0, min)))
    return result
};

console.log(`cutIt(arg): ${cutIt(items)}`)

// Step by step solution
const itemSizes = items.map(({length}) => length);
console.log(`itemSizes: ${itemSizes}`)

const min = Math.min(...itemSizes)
console.log(`min: ${min}`)

const cuted = items.map((item) => {
    return item.substr(0, min);
})

console.log(cuted)

// cleaner solution
function cutItClean(item) {
    const minLength = Math.min(...item.map(x => x.length));
    return item.map(x => x.slice(0, minLength));
}

console.log(`cutItClean(items): ${cutItClean(items)}`)

console.log(`itemSizes: ${items.map(({length}) => length)}`)
console.log(`min: ${Math.min(...items.map(({length}) => length))}`)

// More concise solution
const recuted = items.map(item => item.substr(0, Math.min(...items.map(item => item.length))))

console.log(`cuted items: ${recuted}`)