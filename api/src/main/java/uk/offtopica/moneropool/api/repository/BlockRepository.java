package uk.offtopica.moneropool.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.offtopica.moneropool.api.model.Block;

import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Integer> {
    Optional<Block> findByHeight(Integer height);
}
