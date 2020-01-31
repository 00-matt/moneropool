package uk.offtopica.moneropool;

import lombok.Getter;
import uk.offtopica.moneropool.util.HexUtils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

public class Difficulty implements Comparable<Difficulty> {
    private static final BigInteger BASE =
            new BigInteger("00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);

    @Getter
    private final BigInteger difficulty;

    @Getter
    private final String hex;

    @Getter
    private final BigInteger target;

    public Difficulty(BigInteger difficulty) {
        this.difficulty = difficulty;
        byte[] div = BASE.divide(difficulty).toByteArray();
        byte[] padded = new byte[32];
        System.arraycopy(div, 0, padded, 32 - div.length, div.length);
        byte[] bytes = Arrays.copyOfRange(padded, 0, 4);
        for (int i = 0; i < bytes.length / 2; i++) {
            byte x = bytes[i];
            bytes[i] = bytes[bytes.length - i - 1];
            bytes[bytes.length - i - 1] = x;
        }
        // target = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] &
        // 0xFF);
        target = new BigInteger(1, bytes);
        hex = HexUtils.byteArrayToHexString(bytes);
    }

    public Difficulty(Long difficulty) {
        this(BigInteger.valueOf(difficulty));
    }

    public static Difficulty ofShare(byte[] hash) {
        for (int i = 0; i < hash.length / 2; i++) {
            byte tmp = hash[i];
            hash[i] = hash[hash.length - i - 1];
            hash[hash.length - i - 1] = tmp;
        }
        return new Difficulty(BASE.divide(new BigInteger(1, hash)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Difficulty that = (Difficulty) o;
        return Objects.equals(difficulty, that.difficulty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(difficulty);
    }

    @Override
    public String toString() {
        return getHex();
    }

    @Override
    public int compareTo(Difficulty difficulty) {
        return getDifficulty().compareTo(difficulty.getDifficulty());
    }
}
