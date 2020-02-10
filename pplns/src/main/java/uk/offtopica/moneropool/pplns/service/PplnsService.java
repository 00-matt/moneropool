package uk.offtopica.moneropool.pplns.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.offtopica.moneropool.pplns.model.Block;
import uk.offtopica.moneropool.pplns.model.MinerHashes;
import uk.offtopica.moneropool.pplns.repository.BlockRepository;
import uk.offtopica.moneropool.pplns.repository.ShareRepository;

import java.util.List;

@Component
@Slf4j
public class PplnsService {
    private final BlockRepository blockRepository;
    private final DaemonService daemonService;
    private final ShareRepository shareRepository;
    private final int unlockTime;
    private final int windowSize;

    @Autowired
    public PplnsService(BlockRepository blockRepository,
                        DaemonService daemonService,
                        ShareRepository shareRepository,
                        @Value("${pplns.unlock}") int unlockTime,
                        @Value("${pplns.window}") int windowSize) {
        this.blockRepository = blockRepository;
        this.daemonService = daemonService;
        this.shareRepository = shareRepository;
        // this.unlockTime = unlockTime;
        this.unlockTime = 0;
        this.windowSize = windowSize;
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
            // TODO: Mark as orphan in db.
            return;
        }


        List<MinerHashes> minerHashesList =
                shareRepository.findMinerHashes(block.getDifficulty() * windowSize,
                        block.getCreatedAt().toLocalDateTime());

        long totalHashes = 0;
        for (MinerHashes minerHashes : minerHashesList) {
            totalHashes += minerHashes.getHashes();
        }

        for (MinerHashes minerHashes : minerHashesList) {
            final long thisPayout =
                    Math.round((((double) minerHashes.getHashes()) / totalHashes) * block.getExpectedReward());
            log.info("Paying {} {} moneroj", minerHashes.getWalletAddress(), (thisPayout / 1e12D));
        }
    }
}
