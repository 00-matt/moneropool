package uk.offtopica.moneropool.rpc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.offtopica.moneropool.BlockTemplate;
import uk.offtopica.moneropool.Difficulty;
import uk.offtopica.moneropool.util.HexUtils;
import uk.offtopica.moneropool.util.InvalidHexStringException;

@Data
@EqualsAndHashCode(callSuper = true)
public class DaemonGetBlockTemplateResult extends RpcResult {
    @JsonProperty("blocktemplate_blob")
    private String blockTemplateBlob;

    // TODO: Wide difficulty.
    private Long difficulty;

    @JsonProperty("expected_reward")
    private Long expectedReward;

    private Long height;

    @JsonProperty("prev_hash")
    private String prevHash;

    @JsonProperty("reserved_offset")
    private Integer reservedOffset;

    @JsonProperty("seed_height")
    private Long seedHeight;

    @JsonProperty("seed_hash")
    private String seedHash;

    @JsonProperty("next_seed_hash")
    private String nextSeedHash;

    public BlockTemplate asBlockTemplate() {
        try {
            BlockTemplate blockTemplate = new BlockTemplate();
            blockTemplate.setTemplateBlob(HexUtils.hexStringToByteArray(blockTemplateBlob));
            blockTemplate.setDifficulty(new Difficulty(difficulty));
            blockTemplate.setExpectedReward(expectedReward);
            blockTemplate.setHeight(height);
            blockTemplate.setPrevHash(HexUtils.hexStringToByteArray(prevHash));
            blockTemplate.setReservedOffset(reservedOffset);
            blockTemplate.setSeedHeight(seedHeight);
            blockTemplate.setSeedHash(HexUtils.hexStringToByteArray(seedHash));

            if (nextSeedHash == null || nextSeedHash.length() == 0) {
                blockTemplate.setNextSeedHash(null);
            } else {
                blockTemplate.setNextSeedHash(HexUtils.hexStringToByteArray(nextSeedHash));
            }

            return blockTemplate;
        } catch (InvalidHexStringException e) {
            throw new RuntimeException(e);
        }
    }
}
