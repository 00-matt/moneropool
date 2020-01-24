package uk.offtopica.moneropool;

import lombok.Data;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;

@Data
public class Miner {
    private String username;
    private String password;
    private String agent;
    private Long id;
    private LocalDate connectedAt;
    private BigInteger hashesSubmitted;
    private Long validSharesSubmitted;
    private Long invalidSharesSubmitted;

    public void addInvalidShare() {
        invalidSharesSubmitted++;
    }

    public void addValidShare(Difficulty difficulty) {
        validSharesSubmitted++;
        hashesSubmitted = hashesSubmitted.add(difficulty.getDifficulty());
    }

    public Duration getConnectionPeriod() {
        return Duration.between(connectedAt, LocalDate.now());
    }
}
