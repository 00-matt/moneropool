package uk.offtopica.moneropool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class DifficultyCalculator {
    private BigInteger shareTargetTime;
    private BigInteger minDiff;
    private BigInteger startDiff;
    private int wait;

    public DifficultyCalculator(@Value("${varDiff.shareTargetTime}") long shareTargetTime,
                                @Value("${varDiff.minimum}") long minDiff,
                                @Value("${varDiff.start}") long startDiff,
                                @Value("${varDiff.wait}") int wait) {
        this.shareTargetTime = BigInteger.valueOf(shareTargetTime);
        this.minDiff = BigInteger.valueOf(minDiff);
        this.startDiff = BigInteger.valueOf(startDiff);
        this.wait = wait;
    }

    public Difficulty getNextJobDifficulty(Miner miner) {
        // when few shares have been submitted, use the starting difficulty
        if (miner.getValidSharesSubmitted() < wait) {
            return new Difficulty(startDiff);
        }

        // hashrate = hashes submitted / connection time
        BigInteger hashrate =
                miner.getHashesSubmitted().divide(BigInteger.valueOf(miner.getConnectionPeriod()));

        BigInteger difficulty = hashrate.multiply(shareTargetTime);

        return new Difficulty(difficulty.max(minDiff));
    }
}
