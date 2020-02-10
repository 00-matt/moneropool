package uk.offtopica.moneropool;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.offtopica.moneropool.util.BlockTemplateUtils;
import uk.offtopica.monerorpc.daemon.BlockTemplate;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class JobFactory {
    @Autowired
    private InstanceId instanceId;

    @Setter
    private BlockTemplate blockTemplate;

    @Autowired
    private DifficultyCalculator difficultyCalculator;

    public Job getJob(Miner miner) {
        Job job = new Job();
        job.setId(ThreadLocalRandom.current().nextLong());
        job.setBlob(BlockTemplateUtils.getHashingBlob(blockTemplate, instanceId, miner.getId()));
        // TODO: Calcualted difficulty can be higher than the network difficulty.
        job.setDifficulty(difficultyCalculator.getNextJobDifficulty(miner));
        job.setHeight(blockTemplate.getHeight());
        job.setSeedHash(blockTemplate.getSeedHash());
        job.setTemplate(blockTemplate);
        return job;
    }
}
