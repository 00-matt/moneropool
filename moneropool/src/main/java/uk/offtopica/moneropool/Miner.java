package uk.offtopica.moneropool;

import lombok.Data;

import java.math.BigInteger;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Data
public class Miner {
    private String username;
    private String password;
    private String agent;
    private Long id;
    private Instant connectedAt = Instant.now();
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

    public long getConnectionPeriod() {
        return ChronoUnit.SECONDS.between(connectedAt, Instant.now());
    }
}
