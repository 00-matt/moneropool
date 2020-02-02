package uk.offtopica.moneropool.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.offtopica.moneropool.api.model.Miner;
import uk.offtopica.moneropool.api.repository.ShareRepository;

@Service
public class ShareService {
    private static final int HASHRATE_PERIOD = 200;

    @Autowired
    private ShareRepository shareRepository;

    public long getHashrate() {
        return shareRepository.findHashesSubmitted(HASHRATE_PERIOD) / HASHRATE_PERIOD;
    }

    public long getHashrate(Miner miner) {
        return shareRepository.findHashesSubmitted(miner, HASHRATE_PERIOD) / HASHRATE_PERIOD;
    }

    public long getSharesSubmitted() {
        return shareRepository.count();
    }

    public long getSharesSubmitted(Miner miner) {
        return shareRepository.countByMiner(miner);
    }
}
