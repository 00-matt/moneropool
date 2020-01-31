package uk.offtopica.moneropool.util;

public class ByteArrayUtils {
    public static byte[] longToByteArray(long val) {
        byte[] ret = new byte[Long.BYTES];
        for (int i = Long.BYTES - 1; i >= 0; i--) {
            ret[i] = (byte) (val & 0xFF);
            val >>= 8;
        }
        return ret;
    }
}
