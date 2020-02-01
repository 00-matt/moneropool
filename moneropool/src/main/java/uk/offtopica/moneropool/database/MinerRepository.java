package uk.offtopica.moneropool.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

@Component
public class MinerRepository {
    @Autowired
    private Sql2o sql2o;

    public int ensure(String wallet) {
        try (Connection conn = sql2o.open()) {
            return conn
                    .createQuery("INSERT INTO miner(wallet_address) SELECT :wallet WHERE NOT EXISTS (SELECT " +
                            "wallet_address FROM miner WHERE wallet_address = :wallet);")
                    .addParameter("wallet", wallet)
                    .executeUpdate()
                    .createQuery("SELECT id FROM miner WHERE wallet_address = :wallet LIMIT 1;")
                    .addParameter("wallet", wallet)
                    .executeScalar(Integer.class);
        }
    }
}
