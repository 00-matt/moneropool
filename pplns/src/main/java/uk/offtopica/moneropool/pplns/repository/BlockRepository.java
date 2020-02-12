package uk.offtopica.moneropool.pplns.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import uk.offtopica.moneropool.pplns.model.Block;

import java.util.List;

@Component
public class BlockRepository {
    private final Sql2o sql2o;

    @Autowired
    public BlockRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public List<Block> findUnpaidBlocks(Long height) {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery("SELECT * FROM block WHERE paid = FALSE AND orphaned = FALSE AND height <= " +
                    ":height;")
                    .addParameter("height", height)
                    .setAutoDeriveColumnNames(true)
                    .executeAndFetch(Block.class);
        }
    }

    public void markAsOrphan(Block block) {
        try (Connection conn = sql2o.open()) {
            conn.createQuery("UPDATE block SET orphan = TRUE WHERE block.id = :id;")
                    .addParameter("id", block.getId())
                    .executeUpdate();
        }
    }

    public void markAsPaid(Block block) {
        try (Connection conn = sql2o.open()) {
            conn.createQuery("UPDATE block SET paid = TRUE WHERE block.id = :id;")
                    .addParameter("id", block.getId())
                    .executeUpdate();
        }
    }
}
