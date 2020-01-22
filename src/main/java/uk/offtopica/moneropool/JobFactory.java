package uk.offtopica.moneropool;

import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobFactory {
    @Setter
    private BlockTemplate blockTemplate;

    public Job getJob() {
        Job job = new Job();
        job.setId(ThreadLocalRandom.current().nextLong());
        job.setBlob(blockTemplate.getHashingBlob());
        job.setDifficulty(blockTemplate.getDifficulty());
        job.setHeight(blockTemplate.getHeight());
        job.setSeedHash(blockTemplate.getSeedHash());
        job.setTemplate(blockTemplate);
        return job;
    }
}
