package playground.programming

import java.util.*
import kotlin.test.Test

class Array4FunTest {
    @Test
    fun test_misc() {
//        String[] strArr = {"foo", "bar", "baz", "qux", "quux", "corge", "grault", "garply", "waldo", "fred", "plugh", "xyzzy", "thud"};
//        System.out.println("before : strArr = " + Arrays.toString(strArr));
//        Arrays.binarySearch(Arrays.copyOf(strArr,strArr.length));
//        System.out.println("after : strArr = " + Arrays.toString(strArr));
//        Arrays.sort(strArr);
        val rand = Random()
        // Obtain a number between [0 - 49].
        @Suppress("UNUSED_VARIABLE") val n = rand.nextInt(50)
        val intArr = arrayOfNulls<Int>(50)
        Arrays.setAll(intArr) { rand.nextInt(50) }
        println(intArr.contentToString())
        val intArrCopy = intArr.copyOf(intArr.size)
        Arrays.sort(intArrCopy)
        println(intArrCopy.contentToString())
    }
}