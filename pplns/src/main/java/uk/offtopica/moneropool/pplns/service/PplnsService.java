package uk.offtopica.moneropool.pplns.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.offtopica.moneropool.pplns.model.Block;
import uk.offtopica.moneropool.pplns.model.MinerHashes;
import uk.offtopica.moneropool.pplns.repository.BlockRepository;
import uk.offtopica.moneropool.pplns.repository.ShareRepository;
import uk.offtopica.moneropool.pplns.repository.TransactionRepository;
import uk.offtopica.monerorpc.wallet.TransferDestination;
import uk.offtopica.monerorpc.wallet.TransferResult;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class PplnsService {
    private final BlockRepository blockRepository;
    private final DaemonService daemonService;
    private final ShareRepository shareRepository;
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final int unlockTime;
    private final int windowSize;
    private final long feeDivisor;

    @Autowired
    public PplnsService(BlockRepository blockRepository,
                        DaemonService daemonService,
                        ShareRepository shareRepository,
                        TransactionRepository transactionRepository,
                        WalletService walletService,
                        @Value("${pplns.unlock}") int unlockTime,
                        @Value("${pplns.window}") int windowSize,
                        @Value("${pplns.feeDivisor}") long feeDivisor) {
        this.blockRepository = blockRepository;
        this.daemonService = daemonService;
        this.shareRepository = shareRepository;
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
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

        List<TransferDestination> transferDestinations = new ArrayList<>(minerHashesList.size());

        for (MinerHashes minerHashes : minerHashesList) {
            final long thisPayout =
                    Math.round((((double) minerHashes.getHashes()) / totalHashes) * reward);

            transferDestinations.add(new TransferDestination(minerHashes.getWalletAddress(), thisPayout));
        }

        // Mark as paid before actually sending the transaction. We can always review the logs to resend a payment,
        // but we can't ask for coins back if we send them twice.
        blockRepository.markAsPaid(block);

        List<TransferResult> transferResults = walletService.sendPayment(transferDestinations);

        for (TransferResult transferResult : transferResults) {
            log.info("Transaction hash={}", transferResult.getHash());
            // TODO: Extract to library.
            StringBuilder sb = new StringBuilder();
            for (byte b : transferResult.getHash()) {
                sb.append(String.format("%02x", b));
            }
            transactionRepository.createTransaction(block, sb.toString());
        }
    }
}
