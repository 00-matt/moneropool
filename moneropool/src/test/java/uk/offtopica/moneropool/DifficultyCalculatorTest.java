package uk.offtopica.moneropool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

@MoneroPoolTest
class DifficultyCalculatorTest {
    @Test
    void testGetDifficultyUsesMin() {
        final BigInteger minDiff = BigInteger.valueOf(20_000);
        final DifficultyCalculator difficultyCalculator =
                new DifficultyCalculator(30, 20_000, 1, 0);

        for (int hashrate : List.of(10, 100, 500)) {
            // A miner that has been connected for 120 seconds at hashrate H/s
            Miner miner = new Miner();
            miner.setConnectedAt(Instant.now().minusSeconds(120));
            miner.setHashesSubmitted(BigInteger.valueOf(hashrate * 120));
            miner.setValidSharesSubmitted(4L);
            Assertions.assertEquals(minDiff, difficultyCalculator.getNextJobDifficulty(miner).getDifficulty());
        }
    }

    @Test
    void testGetDifficultyUsesStart() {
        final BigInteger startDiff = BigInteger.valueOf(5000);
        final DifficultyCalculator difficultyCalculator =
                new DifficultyCalculator(1, 1, 5000L, 5);

        for (int i = 0; i < 5; i++) {
            final Miner miner = new Miner();
            miner.setValidSharesSubmitted((long) i);
            miner.setHashesSubmitted(BigInteger.valueOf(100));
            Assertions.assertEquals(startDiff, difficultyCalculator.getNextJobDifficulty(miner).getDifficulty());
        }
    }
}
