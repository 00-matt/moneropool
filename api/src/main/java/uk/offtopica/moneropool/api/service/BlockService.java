package uk.offtopica.moneropool.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.offtopica.moneropool.api.exception.BlockNotFoundException;
import uk.offtopica.moneropool.api.model.Block;
import uk.offtopica.moneropool.api.repository.BlockRepository;

@Service
public class BlockService {
    @Autowired
    private BlockRepository blockRepository;

    public Block findByHeight(Integer height) throws BlockNotFoundException {
        return blockRepository.findByHeight(height).orElseThrow(BlockNotFoundException::new);
    }

    public long getBlockCount() {
        return blockRepository.count();
    }
}
