package uk.offtopica.moneropool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.offtopica.moneropool.rpc.MoneroDaemon;

import java.io.IOException;

@Service
@Slf4j
public class ShareProcessor {
    @Autowired
    private MoneroDaemon daemon;

    @Autowired
    private InstanceId instanceId;

    public ShareStatus process(Miner miner, Job job, byte[] nonce, byte[] result) {
        final Difficulty shareDifficulty = Difficulty.ofShare(result);

        if (shareDifficulty.compareTo(job.getDifficulty()) < 0) {
            return ShareStatus.LOW_DIFFICULTY;
        }

        final byte[] templateBlob = job.getTemplate().withExtra(instanceId, miner.getId(), nonce);

        // TODO: Validate result hash

        if (shareDifficulty.compareTo(job.getTemplate().getDifficulty()) >= 0) {
            log.info("Found block at height {}", job.getHeight());
            try {
                daemon.submitBlock(templateBlob);
            } catch (IOException e) {
                log.error("Failed to submit block", e);
            }
        }

        return ShareStatus.VALID;
    }
}
