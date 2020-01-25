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
    private LocalDate connectedAt = LocalDate.now();
    private BigInteger hashesSubmitted = BigInteger.ZERO;
    private Long validSharesSubmitted = 0L;
    private Long invalidSharesSubmitted = 0L;

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
