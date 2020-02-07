package uk.offtopica.moneropool.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import uk.offtopica.moneropool.util.HexUtils;

@Component
public class BlockRepository {
    @Autowired
    private Sql2o sql2o;

    public void insert(byte[] hash, int height, long expectedReward, long difficulty) {
        try (Connection conn = sql2o.open()) {
            conn.createQuery(
                    "INSERT INTO block(hash, height, expected_reward, difficulty) " +
                            "VALUES(:hash, :height, :expectedReward, :difficulty);")
                    .addParameter("hash", HexUtils.byteArrayToHexString(hash))
                    .addParameter("height", height)
                    .addParameter("expectedReward", expectedReward)
                    .addParameter("difficulty", difficulty)
                    .executeUpdate();
        }
    }
}
