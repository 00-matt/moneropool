package uk.offtopica.moneropool.pplns.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

@Component
public class RewardRepository {
    private final Sql2o sql2o;

    @Autowired
    public RewardRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public void createReward(Integer minerId, Long amount) {
        try (Connection conn = sql2o.open()) {
            conn.createQuery("INSERT INTO reward (miner_id, amount) VALUES (:miner, :amount);")
                    .addParameter("miner", minerId)
                    .addParameter("amount", amount)
                    .executeUpdate();
        }
    }
}
