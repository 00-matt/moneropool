package uk.offtopica.moneropool.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MinerStats {
    @JsonProperty("first_seen")
    private LocalDateTime createdAt;

    @JsonProperty("estimated_hashrate")
    private Long hashrate;

    @JsonProperty("valid_shares")
    private Long validShares;
}
