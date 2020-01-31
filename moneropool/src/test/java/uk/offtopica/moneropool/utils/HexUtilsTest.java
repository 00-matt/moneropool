package uk.offtopica.moneropool.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.offtopica.moneropool.util.HexUtils;
import uk.offtopica.moneropool.util.InvalidHexStringException;

import java.util.stream.Stream;

class HexUtilsTest {
    static Stream<Arguments> valid() {
        return Stream.of(
                Arguments.of("", new byte[]{}),
                Arguments.of("00", new byte[]{0x00}),
                Arguments.of("01", new byte[]{0x01}),
                Arguments.of("ff", new byte[]{(byte) 0xFF}),
                Arguments.of("deadbeef", new byte[]{(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF}),
                Arguments.of("DEADBEEF", new byte[]{(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF})
        );
    }

    static Stream<Arguments> invalid() {
        return Stream.of(
                // invalid length
                Arguments.of("0"),
                Arguments.of("a"),
                Arguments.of("caf"),
                Arguments.of("deadbee"),
                // invalid character
                Arguments.of("deadbeex"),
                Arguments.of("f$")
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("uk.offtopica.moneropool.utils.HexUtilsTest#valid")
    void testByteArrayToHexString(String hex, byte[] bytes) {
        final String actual = HexUtils.byteArrayToHexString(bytes);
        Assertions.assertEquals(hex.toLowerCase(), actual.toLowerCase());
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("uk.offtopica.moneropool.utils.HexUtilsTest#valid")
    void testHexStringToByteArrayValid(String hex, byte[] bytes) throws InvalidHexStringException {
        final byte[] actual = HexUtils.hexStringToByteArray(hex);
        Assertions.assertArrayEquals(bytes, actual);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("uk.offtopica.moneropool.utils.HexUtilsTest#invalid")
    void testHexStringToByteArrayInvalid(String hex) {
        Assertions.assertThrows(InvalidHexStringException.class, () -> {
            HexUtils.hexStringToByteArray(hex);
        });
    }
}
