package uk.offtopica.moneropool.util;

public class NativeUtils {
    static {
        System.loadLibrary("moneropool");
    }

    public static native byte[] getHashingBlob(byte[] template);
}
