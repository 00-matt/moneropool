package uk.offtopica.moneropool.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.offtopica.moneropool.api.exception.MinerNotFoundException;
import uk.offtopica.moneropool.api.model.MinerStats;
import uk.offtopica.moneropool.api.service.StatsService;

@RestController
public class StatsController {
    @Autowired
    private StatsService statsService;

    @GetMapping("/stats/{walletAddress}")
    public MinerStats getMinerHashrate(@PathVariable String walletAddress) {
        try {
            return statsService.findStats(walletAddress);
        } catch (MinerNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Miner not found", e);
        }
    }
}
