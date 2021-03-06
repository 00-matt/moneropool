package uk.offtopica.moneropool.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.offtopica.moneropool.InstanceId;
import uk.offtopica.moneropool.util.BlockTemplateUtils;
import uk.offtopica.moneropool.util.HexUtils;
import uk.offtopica.moneropool.util.InvalidHexStringException;
import uk.offtopica.monerorpc.daemon.BlockTemplate;

import java.math.BigInteger;

class BlockTemplateUtilsTest {
    static final int RESERVED_OFFSET = 129;
    static byte[] TEMPLATE;

    static {
        try {
            TEMPLATE = HexUtils.hexStringToByteArray(
                    "0c0cfafca6f1057ee6321c82607b38ed75d26f9b9ede1ca8355dd60049134ca6014c8e1e1d2b850000000002d9d11e01ff9" +
                            "dd11e01b9d68ee2d49503021d80124e781d89d3f7d32d1d64b0dfe81bd2697fd05d7fc1c86088513e478c545f01" +
                            "b1a815ed592f68219f38b4b81ee7fc27121517d10570c939e45ebe1b88727bb3023c00000000000000000000000" +
                            "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" +
                            "0000000000");
        } catch (InvalidHexStringException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetHashingBlob() throws InvalidHexStringException {
        BlockTemplate bt = new BlockTemplate(
                TEMPLATE,
                new byte[]{},
                BigInteger.ZERO,
                0L,
                0L,
                null,
                new byte[]{},
                RESERVED_OFFSET,
                new byte[]{},
                0L
        );
        byte[] expected = HexUtils.hexStringToByteArray(
                "0c0cfafca6f1057ee6321c82607b38ed75d26f9b9ede1ca8355dd60049134ca6014c8e1e1d2b850000000067f9c7b96f152" +
                        "ec3dcbe57b194f0e000be47982b387f874646dfaf545849af1b01");
        byte[] actual = BlockTemplateUtils.getHashingBlob(bt, new InstanceId(123456L), 654321L);
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    void testWithExtra() throws InvalidHexStringException {
        BlockTemplate bt = new BlockTemplate(
                TEMPLATE,
                new byte[]{},
                BigInteger.ZERO,
                0L,
                0L,
                null,
                new byte[]{},
                RESERVED_OFFSET,
                new byte[]{},
                0L
        );
        byte[] expected = HexUtils.hexStringToByteArray("0c0cfafca6f1057ee6321c82607b38ed75d26f9b9ede1ca8355dd600491" +
                "34ca6014c8e1e1d2b85cafebabe02d9d11e01ff9dd11e01b9d68ee2d49503021d80124e781d89d3f7d32d1d64b0dfe81bd2" +
                "697fd05d7fc1c86088513e478c545f01b1a815ed592f68219f38b4b81ee7fc27121517d10570c939e45ebe1b88727bb3023" +
                "c000000000001e240000000000009fbf1000000000000000000000000000000000000000000000000000000000000000000" +
                "00000000000000000000000000");
        byte[] actual = BlockTemplateUtils.withExtra(bt, new InstanceId(123456L), 654321L,
                HexUtils.hexStringToByteArray("cafebabe"));
        Assertions.assertArrayEquals(expected, actual);
    }
}
