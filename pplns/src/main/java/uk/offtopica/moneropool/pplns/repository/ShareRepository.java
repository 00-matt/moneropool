package uk.offtopica.moneropool.pplns.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import uk.offtopica.moneropool.pplns.model.MinerHashes;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ShareRepository {
    private final Sql2o sql2o;

    @Autowired
    public ShareRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public List<MinerHashes> findMinerHashes(Long windowWidth, LocalDateTime before) {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(
                    "SELECT wallet_address, SUM(difficulty) AS hashes FROM ( " +
                            "SELECT " +
                            "  miner.wallet_address AS wallet_address, " +
                            "  difficulty, " +
                            "  ( SELECT SUM(difficulty) FROM share WHERE id > s.id AND created_at <= :before ) AS " +
                            "total " +
                            "FROM share s " +
                            "INNER JOIN miner ON miner.id = s.miner_id " +
                            "WHERE s.created_at <= :before" +
                            ") s1 " +
                            "WHERE s1.total < :windowWidth " +
                            "GROUP BY wallet_address;")
                    .addParameter("windowWidth", windowWidth)
                    .addParameter("before", before)
                    .setAutoDeriveColumnNames(true)
                    .executeAndFetch(MinerHashes.class);
        }
    }
}
