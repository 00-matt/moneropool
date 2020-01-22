package uk.offtopica.moneropool;

import lombok.Data;

@Data
public class BlockTemplate {
    private byte[] templateBlob;
    private byte[] hashingBlob;
    private Difficulty difficulty;
    private Long expectedReward;
    private Long height;
    private byte[] prevHash;
    private Integer reservedOffset;
    private Long seedHeight;
    private byte[] seedHash;
}
