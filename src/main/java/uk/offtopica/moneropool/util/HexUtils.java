package uk.offtopica.moneropool.util;

public class HexUtils {
    private static final char[] ALPHABET = "0123456789abcdef".toCharArray();

    public static String byteArrayToHexString(byte[] array) {
        char[] string = new char[array.length * 2];

        for (int i = 0; i < array.length; i++) {
            int x = array[i] & 0xFF;
            string[i * 2] = ALPHABET[x >>> 4];
            string[i * 2 + 1] = ALPHABET[x & 0x0F];
        }

        return new String(string);
    }

    public static byte[] hexStringToByteArray(String hex) {
        byte[] array = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            array[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return array;
    }
}
