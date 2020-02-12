package uk.offtopica.moneropool.pplns.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Block {
    private Integer id;
    private Integer height;
    private String hash;
    private Boolean paid;
    private Boolean orphaned;
    private Long expectedReward;
    private Long difficulty;
    private Timestamp createdAt;
}
