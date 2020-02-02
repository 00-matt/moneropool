package uk.offtopica.moneropool.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.offtopica.moneropool.api.exception.MinerNotFoundException;
import uk.offtopica.moneropool.api.model.Miner;
import uk.offtopica.moneropool.api.repository.MinerRepository;

@Service
public class MinerService {
    @Autowired
    private MinerRepository minerRepository;

    public Miner findByWalletAddress(String walletAddress) throws MinerNotFoundException {
        return minerRepository.findByWalletAddress(walletAddress).orElseThrow(MinerNotFoundException::new);
    }
}
