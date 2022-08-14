package playground.misc;

import java.util.Arrays;
import java.util.Random;

public class JMisc {
    public static void main(String[] main) {
//        String[] strArr = {"foo", "bar", "baz", "qux", "quux", "corge", "grault", "garply",
//        "waldo", "fred", "plugh", "xyzzy", "thud"};
//        System.out.println("before : strArr = " + Arrays.toString(strArr));
//        Arrays.binarySearch(Arrays.copyOf(strArr,strArr.length));
//        System.out.println("after : strArr = " + Arrays.toString(strArr));
//        Arrays.sort(strArr);

        Random rand = new Random();
// Obtain a number between [0 - 49].
        int n = rand.nextInt(50);
        Integer[] intArr = new Integer[50];
        Arrays.setAll(intArr, (int i) -> rand.nextInt(50));
        System.out.println(Arrays.toString(intArr));
        Integer[] intArrCopy = Arrays.copyOf(intArr, intArr.length);
        Arrays.sort(intArrCopy);
        System.out.println(Arrays.toString(intArrCopy));
    }
}
