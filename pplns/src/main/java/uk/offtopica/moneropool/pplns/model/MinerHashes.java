package uk.offtopica.moneropool.pplns.model;

import lombok.Data;

@Data
public class MinerHashes {
    private String walletAddress;
    private Long hashes;
}
