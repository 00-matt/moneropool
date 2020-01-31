package uk.offtopica.moneropool.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.offtopica.moneropool.util.HexUtils;
import uk.offtopica.moneropool.util.InvalidHexStringException;
import uk.offtopica.moneropool.util.NativeUtils;

class NativeUtilsTest {
    @Test
    void testGetHashingBlob() throws InvalidHexStringException {
        byte[] template = HexUtils.hexStringToByteArray(
                "0c0cfafca6f1057ee6321c82607b38ed75d26f9b9ede1ca8355dd60049134ca6014c8e1e1d2b850000000002d9d11e01ff9" +
                        "dd11e01b9d68ee2d49503021d80124e781d89d3f7d32d1d64b0dfe81bd2697fd05d7fc1c86088513e478c545f01" +
                        "b1a815ed592f68219f38b4b81ee7fc27121517d10570c939e45ebe1b88727bb3023c00000000000000000000000" +
                        "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" +
                        "0000000000");
        byte[] expected = HexUtils.hexStringToByteArray(
                "0c0cfafca6f1057ee6321c82607b38ed75d26f9b9ede1ca8355dd60049134ca6014c8e1e1d2b8500000000eaa9c70ed820c" +
                        "4d96fad3c15013fdc3978410cad22a2dc1994275e422bb7374201");
        byte[] actual = NativeUtils.getHashingBlob(template);
        Assertions.assertArrayEquals(expected, actual);
    }
}
