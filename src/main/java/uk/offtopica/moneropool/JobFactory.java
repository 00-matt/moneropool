package uk.offtopica.moneropool;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class JobFactory {
    @Autowired
    private InstanceId instanceId;

    @Setter
    private BlockTemplate blockTemplate;

    public Job getJob(Miner miner) {
        Job job = new Job();
        job.setId(ThreadLocalRandom.current().nextLong());
        job.setBlob(blockTemplate.getHashingBlob(instanceId, miner.getId()));
        job.setDifficulty(blockTemplate.getDifficulty());
        job.setHeight(blockTemplate.getHeight());
        job.setSeedHash(blockTemplate.getSeedHash());
        job.setTemplate(blockTemplate);
        return job;
    }
}
