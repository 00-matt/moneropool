package uk.offtopica.moneropool.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PoolStats {
    @JsonProperty("estimated_hashrate")
    private Long hashrate;

    @JsonProperty("total_miner_count")
    private Long minerCount;

    @JsonProperty("total_block_count")
    private Long blockCount;
}
