package uk.offtopica.moneropool.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.offtopica.moneropool.api.exception.MinerNotFoundException;
import uk.offtopica.moneropool.api.model.Miner;
import uk.offtopica.moneropool.api.model.MinerStats;
import uk.offtopica.moneropool.api.model.PoolStats;

@Service
public class StatsService {
    @Autowired
    private BlockService blockService;

    @Autowired
    private MinerService minerService;

    @Autowired
    private ShareService shareService;

    public MinerStats findMinerStats(String walletAddress) throws MinerNotFoundException {
        final Miner miner = minerService.findByWalletAddress(walletAddress);
        final Long hashrate = shareService.getHashrate(miner);
        final Long shares = shareService.getSharesSubmitted(miner);
        final MinerStats minerStats = new MinerStats();
        minerStats.setCreatedAt(miner.getCreatedAt());
        minerStats.setHashrate(hashrate);
        minerStats.setValidShares(shares);
        return minerStats;
    }

    public PoolStats findPoolStats() {
        final Long hashrate = shareService.getHashrate();
        final Long minerCount = minerService.findMinerCount();
        final Long blockCount = blockService.getBlockCount();
        final PoolStats poolStats = new PoolStats();
        poolStats.setHashrate(hashrate);
        poolStats.setMinerCount(minerCount);
        poolStats.setBlockCount(blockCount);
        return poolStats;
    }
}
