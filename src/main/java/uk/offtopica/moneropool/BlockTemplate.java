package uk.offtopica.moneropool;

import lombok.Data;
import uk.offtopica.moneropool.util.ByteArrayUtils;
import uk.offtopica.moneropool.util.NativeUtils;

@Data
public class BlockTemplate {
    private byte[] templateBlob;
    private Difficulty difficulty;
    private Long expectedReward;
    private Long height;
    private byte[] prevHash;
    private Integer reservedOffset;
    private Long seedHeight;
    private byte[] seedHash;
    private byte[] nextSeedHash;

    public byte[] getHashingBlob(InstanceId instanceId, Long minerId) {
        return NativeUtils.getHashingBlob(withExtra(instanceId, minerId, new byte[]{}));
    }

    public byte[] getHashingBlob(InstanceId instanceId, Long minerId, byte[] nonce) {
        return NativeUtils.getHashingBlob(withExtra(instanceId, minerId, nonce));
    }

    public byte[] withExtra(InstanceId instanceId, Long minerId, byte[] nonce) {
        byte[] template = templateBlob.clone();

        System.arraycopy(nonce, 0, template, 39, Math.min(nonce.length, 4));
        System.arraycopy(instanceId.getBytes(), 0, template, reservedOffset, instanceId.getBytes().length);
        byte[] minerIdBytes = ByteArrayUtils.longToByteArray(minerId);
        System.arraycopy(minerIdBytes, 0, template, reservedOffset + instanceId.getBytes().length,
                minerIdBytes.length);

        return template;
    }
}
