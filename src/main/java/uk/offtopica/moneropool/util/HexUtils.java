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

    public static byte[] hexStringToByteArray(String hex) throws InvalidHexStringException {
        if (hex.length() % 2 != 0) {
            throw new InvalidHexStringException("Invalid length");
        }

        byte[] array = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            final int n1 = Character.digit(hex.charAt(i), 16);
            final int n2 = Character.digit(hex.charAt(i + 1), 16);

            if (n1 == -1 || n2 == -1) {
                throw new InvalidHexStringException("Invalid character");
            }

            array[i / 2] = (byte) ((n1 << 4) + n2);
        }
        return array;
    }
}
