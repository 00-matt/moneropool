package uk.offtopica.moneropool;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Job {
    private Long id;
    private byte[] blob;
    private Difficulty difficulty;
    private byte[] seedHash;
    private Long height;
    private BlockTemplate template;
    private Set<byte[]> results = new HashSet<>();
}
