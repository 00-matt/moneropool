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

    public void insert(byte[] hash, int height) {
        try (Connection conn = sql2o.open()) {
            conn.createQuery("INSERT INTO block(hash, height) VALUES(:hash, :height);")
                    .addParameter("hash", HexUtils.byteArrayToHexString(hash))
                    .addParameter("height", height)
                    .executeUpdate();
        }
    }
}
