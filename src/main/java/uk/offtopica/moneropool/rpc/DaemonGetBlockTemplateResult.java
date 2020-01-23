package uk.offtopica.moneropool.rpc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.offtopica.moneropool.BlockTemplate;
import uk.offtopica.moneropool.Difficulty;
import uk.offtopica.moneropool.util.HexUtils;

@Data
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

    public BlockTemplate asBlockTemplate() {
        BlockTemplate blockTemplate = new BlockTemplate();

        blockTemplate.setTemplateBlob(HexUtils.hexStringToByteArray(blockTemplateBlob));
        blockTemplate.setDifficulty(new Difficulty(difficulty));
        blockTemplate.setExpectedReward(expectedReward);
        blockTemplate.setHeight(height);
        blockTemplate.setPrevHash(HexUtils.hexStringToByteArray(prevHash));
        blockTemplate.setReservedOffset(reservedOffset);
        blockTemplate.setSeedHeight(seedHeight);
        blockTemplate.setSeedHash(HexUtils.hexStringToByteArray(seedHash));

        return blockTemplate;
    }
}
