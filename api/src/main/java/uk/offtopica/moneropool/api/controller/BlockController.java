package uk.offtopica.moneropool.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.offtopica.moneropool.api.exception.BlockNotFoundException;
import uk.offtopica.moneropool.api.model.Block;
import uk.offtopica.moneropool.api.service.BlockService;

import java.util.List;

@RestController
@RequestMapping("/block")
public class BlockController {
    @Autowired
    private BlockService blockService;

    @GetMapping("/recent")
    public List<Block> getRecent() {
        return blockService.findAllRecent(0).getContent();
    }

    @GetMapping("/{height}")
    public Block getBlockByHeight(@PathVariable Integer height) {
        try {
            return blockService.findByHeight(height);
        } catch (BlockNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Block not found", e);
        }
    }
}
