//List of Katas
// https://github.com/web-codes/Katas-list-of-Training-JS-series/blob/master/README.md
console.log(`Playground`)
/*==============================????========================================*/
const array1 = [121, 144, 19, 161, 19, 144, 19, 11];
const array2 = [11 * 11, 121 * 121, 144 * 144, 19 * 19,
    161 * 161, 19 * 19, 144 * 144, 19 * 19];

const comp = (array1, array2) =>
    array2 == null
    || array1 == null
    || array1[0] == null
    || array2[0] == null
        ? false : array1[0] === array2[0];

console.log(array1[0])
console.log(array2[0])
console.log(comp(array1, array2))
/*======================================================================*/


