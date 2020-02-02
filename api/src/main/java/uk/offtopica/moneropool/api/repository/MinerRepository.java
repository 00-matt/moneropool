package uk.offtopica.moneropool.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.offtopica.moneropool.api.model.Miner;

import java.util.Optional;

public interface MinerRepository extends JpaRepository<Miner, Integer> {
    Optional<Miner> findByWalletAddress(String walletAddress);
}
