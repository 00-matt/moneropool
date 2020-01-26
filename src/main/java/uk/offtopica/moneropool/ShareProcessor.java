package uk.offtopica.moneropool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.offtopica.moneropool.hash.ResultHashValidator;
import uk.offtopica.moneropool.rpc.MoneroDaemon;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ShareProcessor {
    @Autowired
    private MoneroDaemon daemon;

    @Autowired
    private InstanceId instanceId;

    @Autowired
    private ResultHashValidator resultHashValidator;

    public CompletableFuture<ShareStatus> process(Miner miner, Job job, byte[] nonce, byte[] result) {
        final Difficulty shareDifficulty = Difficulty.ofShare(result);

        if (shareDifficulty.compareTo(job.getDifficulty()) < 0) {
            miner.addInvalidShare();
            return CompletableFuture.completedFuture(ShareStatus.LOW_DIFFICULTY);
        }

        return resultHashValidator.validate(result, job.getTemplate().getHashingBlob(instanceId, miner.getId(), nonce))
                .thenApply(valid -> {
                    if (!valid) {
                        return ShareStatus.BAD_HASH;
                    }

                    if (shareDifficulty.compareTo(job.getTemplate().getDifficulty()) >= 0) {
                        log.info("Found block at height {}", job.getHeight());
                        try {
                            final byte[] templateBlob = job.getTemplate().withExtra(instanceId, miner.getId(), nonce);
                            daemon.submitBlock(templateBlob);
                        } catch (IOException e) {
                            log.error("Failed to submit block", e);
                        }
                    }

                    miner.addValidShare(job.getDifficulty());

                    return ShareStatus.VALID;
                });
    }
}
