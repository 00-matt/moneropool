package uk.offtopica.moneropool.pplns.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import uk.offtopica.moneropool.pplns.model.Block;

@Component
public class TransactionRepository {
    private final Sql2o sql2o;

    @Autowired
    private TransactionRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public void createTransaction(Block block, String hash) {
        try (Connection conn = sql2o.open()) {
            conn.createQuery("INSERT INTO transaction (block_id, hash) VALUES (:block, :hash);")
                    .addParameter("block", block.getId())
                    .addParameter("hash", hash)
                    .executeUpdate();
        }
    }
}
