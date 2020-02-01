package uk.offtopica.moneropool.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

@Component
public class BlockRepository {
    @Autowired
    private Sql2o sql2o;

    public void insert(int height) {
        try (Connection conn = sql2o.open()) {
            conn.createQuery("INSERT INTO block(height) VALUES(:height);")
                    .addParameter("height", height)
                    .executeUpdate();
        }
    }
}
