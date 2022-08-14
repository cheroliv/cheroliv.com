/*=====================Training JS #17===================================*/
const strDataFirstToLastFun = "ababc"
console.log(strDataFirstToLastFun)
console.log(strDataFirstToLastFun.split("").reverse().join(""))

const firstToLast = (str, c) =>
    str.indexOf(c) === -1 ?
        -1 :
        str.lastIndexOf(c) - str.indexOf(c);

console.log(firstToLast(strDataFirstToLastFun, 'a'))
/*======================================================================*/
