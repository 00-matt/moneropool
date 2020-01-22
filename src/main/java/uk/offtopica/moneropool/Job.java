package uk.offtopica.moneropool;

import lombok.Data;

@Data
public class Job {
    private Long id;
    private byte[] blob;
    private Difficulty difficulty;
    private byte[] seedHash;
    private Long height;
    private BlockTemplate template;
}
