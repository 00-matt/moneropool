package uk.offtopica.moneropool.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.offtopica.moneropool.api.model.Miner;
import uk.offtopica.moneropool.api.model.Share;

public interface ShareRepository extends JpaRepository<Share, Long> {
    @Query(
            value = "SELECT SUM(difficulty) FROM share WHERE created_at > NOW() - (interval '1' second) * ?1",
            nativeQuery = true
    )
    long findHashesSubmitted(int period);

    @Query(
            value = "SELECT SUM(difficulty) FROM share WHERE miner_id = ?1 AND created_at > NOW() - (interval '1' " +
                    "second) * ?2",
            nativeQuery = true
    )
    long findHashesSubmitted(Miner miner, int period);

    long countByMiner(Miner miner);
}
