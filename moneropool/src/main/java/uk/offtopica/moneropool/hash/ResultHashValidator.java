package uk.offtopica.moneropool.hash;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import uk.offtopica.moneropool.NewBlockTemplateEvent;

import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class ResultHashValidator implements ApplicationListener<NewBlockTemplateEvent> {
    private RandomXService service;
    private byte[] lastSeedHash = null;

    public ResultHashValidator(@Value("${randomx.address}") URI uri) {
        service = new RandomXService(uri);
    }

    public CompletableFuture<Boolean> validate(byte[] candidate, byte[] data) {
        return service.hash(data, lastSeedHash)
                .thenApply(hash -> {
                    if (hash == null) {
                        return false;
                    }

                    return Arrays.equals(candidate, hash);
                });
    }

    @Override
    public void onApplicationEvent(NewBlockTemplateEvent event) {
        lastSeedHash = event.getBlockTemplate().getSeedHash();
        service.seed(lastSeedHash);
    }
}
