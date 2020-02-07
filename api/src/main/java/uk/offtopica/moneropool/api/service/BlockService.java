package uk.offtopica.moneropool.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uk.offtopica.moneropool.api.exception.BlockNotFoundException;
import uk.offtopica.moneropool.api.model.Block;
import uk.offtopica.moneropool.api.repository.BlockRepository;

@Service
public class BlockService {
    @Autowired
    private BlockRepository blockRepository;

    public Page<Block> findAllRecent(Integer page) {
        // TODO: Make count configurable?
        return blockRepository.findAll(PageRequest.of(page, 100, Sort.by("id").descending()));
    }

    public Block findByHeight(Integer height) throws BlockNotFoundException {
        return blockRepository.findByHeight(height).orElseThrow(BlockNotFoundException::new);
    }

    public long getBlockCount() {
        return blockRepository.count();
    }
}
