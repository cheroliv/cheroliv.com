package playground.math

import groovy.transform.CompileStatic

@CompileStatic
class GAlgebraUtils {

    /**
     * Great common divisor
     * Plus grand commun diviseur(pgcd)
     * Great Common Divisor
     * @param a
     * @param b
     * @return
     */
    static Integer gcd(Integer a, Integer b) {
        !b ? a : gcd(b, a % b)
    }


    /**
     * Least common multiple
     * Plus petit commun multiplicateur(ppmc)
     * @param a
     * @param b
     * @return
     */
    static BigInteger lcm(Integer a, Integer b) {
        (a * b / gcd(a, b)) as BigInteger
    }

    static void main(String... args) {
        Integer a = 96
        Integer b = 28
        println "gcd(96, 28) = ${gcd(a, b)}"
        a = 790
        b = 990
        println "lcm(790, 990) = ${lcm(a, b)}"
    }
}
