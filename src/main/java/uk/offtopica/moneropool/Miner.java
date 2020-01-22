package uk.offtopica.moneropool;

import lombok.Data;

@Data
public class Miner {
    private String username;
    private String password;
    private String agent;
    private Long id;
}
