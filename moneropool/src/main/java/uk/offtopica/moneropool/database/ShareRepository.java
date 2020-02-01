package uk.offtopica.moneropool.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

@Component
public class ShareRepository {
    @Autowired
    private Sql2o sql2o;

    public void insert(int minerId, long difficulty) {
        try (Connection conn = sql2o.open()) {
            conn.createQuery("INSERT INTO share(miner_id, difficulty) VALUES(:miner_id, :difficulty);")
                    .addParameter("miner_id", minerId)
                    .addParameter("difficulty", difficulty)
                    .executeUpdate();
        }
    }
}
