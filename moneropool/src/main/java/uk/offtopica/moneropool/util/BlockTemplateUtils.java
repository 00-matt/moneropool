package uk.offtopica.moneropool.util;

import uk.offtopica.moneropool.InstanceId;
import uk.offtopica.monerorpc.daemon.BlockTemplate;

public class BlockTemplateUtils {
    public static byte[] getHashingBlob(BlockTemplate blockTemplate, InstanceId instanceId, Long minerId) {
        return NativeUtils.getHashingBlob(withExtra(blockTemplate, instanceId, minerId, new byte[]{}));
    }

    public static byte[] getHashingBlob(BlockTemplate blockTemplate, InstanceId instanceId, Long minerId, byte[] nonce) {
        return NativeUtils.getHashingBlob(withExtra(blockTemplate, instanceId, minerId, nonce));
    }

    public static byte[] withExtra(BlockTemplate blockTemplate, InstanceId instanceId, Long minerId, byte[] nonce) {
        byte[] template = blockTemplate.getBlockTemplateBlob().clone();

        System.arraycopy(nonce, 0, template, 39, Math.min(nonce.length, 4));
        System.arraycopy(instanceId.getBytes(), 0, template, blockTemplate.getReservedOffset(),
                instanceId.getBytes().length);
        byte[] minerIdBytes = ByteArrayUtils.longToByteArray(minerId);
        System.arraycopy(minerIdBytes, 0, template, blockTemplate.getReservedOffset() + instanceId.getBytes().length,
                minerIdBytes.length);

        return template;
    }
}
