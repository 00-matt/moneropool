package uk.offtopica.moneropool.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.offtopica.moneropool.api.exception.MinerNotFoundException;
import uk.offtopica.moneropool.api.model.Miner;
import uk.offtopica.moneropool.api.model.MinerStats;

@Service
public class StatsService {
    @Autowired
    private MinerService minerService;

    @Autowired
    private ShareService shareService;

    public MinerStats findStats(String walletAddress) throws MinerNotFoundException {
        final Miner miner = minerService.findByWalletAddress(walletAddress);
        final Long hashrate = shareService.getHashrate(miner);
        final Long shares = shareService.getSharesSubmitted(miner);
        final MinerStats minerStats = new MinerStats();
        minerStats.setCreatedAt(miner.getCreatedAt());
        minerStats.setHashrate(hashrate);
        minerStats.setValidShares(shares);
        return minerStats;
    }
}
