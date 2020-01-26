package uk.offtopica.moneropool.util;

public class ArrayUtils {
    /**
     * Reverse a byte array in place.
     *
     * @param array Array to reverse. Is modified.
     * @return array
     */
    public static byte[] reverse(byte[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            byte tmp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = tmp;
        }

        return array;
    }

    /**
     * Reverse an array in place.
     *
     * @param array Array to reverse. Is modified.
     * @param <T>   Type of array.
     * @return array
     */
    public static <T> T[] reverse(T[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            T tmp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = tmp;
        }

        return array;
    }
}
