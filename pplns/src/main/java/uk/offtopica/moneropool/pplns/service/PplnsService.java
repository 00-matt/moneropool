package uk.offtopica.moneropool.pplns.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.offtopica.moneropool.pplns.model.Block;
import uk.offtopica.moneropool.pplns.model.MinerHashes;
import uk.offtopica.moneropool.pplns.repository.BlockRepository;
import uk.offtopica.moneropool.pplns.repository.RewardRepository;
import uk.offtopica.moneropool.pplns.repository.ShareRepository;

import java.util.List;

@Component
@Slf4j
public class PplnsService {
    private final BlockRepository blockRepository;
    private final DaemonService daemonService;
    private final RewardRepository rewardRepository;
    private final ShareRepository shareRepository;
    private final int unlockTime;
    private final int windowSize;
    private final long feeDivisor;

    @Autowired
    public PplnsService(BlockRepository blockRepository,
                        DaemonService daemonService,
                        RewardRepository rewardRepository,
                        ShareRepository shareRepository,
                        @Value("${pplns.unlock}") int unlockTime,
                        @Value("${pplns.window}") int windowSize,
                        @Value("${pplns.feeDivisor}") long feeDivisor) {
        this.blockRepository = blockRepository;
        this.daemonService = daemonService;
        this.shareRepository = shareRepository;
        this.rewardRepository = rewardRepository;
        this.unlockTime = unlockTime;
        this.windowSize = windowSize;
        this.feeDivisor = feeDivisor;
    }

    public void processAllPayments() {
        long currentHeight = daemonService.getBlockHeight();
        List<Block> unpaid = blockRepository.findUnpaidBlocks(currentHeight - unlockTime);
        log.info("Processing {} unpaid blocks", unpaid.size());
        unpaid.forEach(this::processPaymentsForBlock);
    }

    public void processPaymentsForBlock(Block block) {
        log.info("Processing payments for {}", block);

        if (daemonService.isOrphan(block)) {
            log.warn("Block #{} is an orphan", block.getId());
            blockRepository.markAsOrphan(block);
            return;
        }

        final long fee = block.getExpectedReward() / 100L;
        final long reward = block.getExpectedReward() - fee;

        log.info("Taking fee of {} moneroj from block reward", fee / 1e12);

        List<MinerHashes> minerHashesList =
                shareRepository.findMinerHashes(block.getDifficulty() * windowSize,
                        block.getCreatedAt().toLocalDateTime());

        long totalHashes = 0;
        for (MinerHashes minerHashes : minerHashesList) {
            totalHashes += minerHashes.getHashes();
        }

        // Mark as paid before actually sending the transaction. We can always review the logs to resend a payment,
        // but we can't ask for coins back if we send them twice.
        blockRepository.markAsPaid(block);

        for (MinerHashes minerHashes : minerHashesList) {
            final long thisPayout =
                    Math.round((((double) minerHashes.getHashes()) / totalHashes) * reward);

            log.info("Creating reward of {} moneroj for miner #{}", thisPayout / 1e12, minerHashes.getMinerId());

            rewardRepository.createReward(minerHashes.getMinerId(), thisPayout);
        }
    }
}
