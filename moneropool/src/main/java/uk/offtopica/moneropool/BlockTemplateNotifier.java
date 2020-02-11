package uk.offtopica.moneropool;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import uk.offtopica.moneropool.notify.NewNetworkBlockEvent;
import uk.offtopica.monerorpc.daemon.BlockTemplate;
import uk.offtopica.monerorpc.daemon.MoneroDaemonRpcClient;

import java.util.Arrays;

@Component
@Slf4j
public class BlockTemplateNotifier implements ApplicationListener<NewNetworkBlockEvent> {
    private static final int RESERVE_SIZE = 20;

    @Autowired
    private MoneroDaemonRpcClient moneroDaemon;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Value("${blocktemplate.address}")
    private String walletAddress;

    @Getter
    private BlockTemplate lastBlockTemplate;

    public void update() {
        log.trace("Updating block template");
        try {
            BlockTemplate candidate = moneroDaemon.getBlockTemplate(walletAddress, RESERVE_SIZE).get();
            if (lastBlockTemplate == null || !Arrays.equals(lastBlockTemplate.getPrevHash(), candidate.getPrevHash())) {
                lastBlockTemplate = candidate;
                applicationEventPublisher.publishEvent(new NewBlockTemplateEvent(this, lastBlockTemplate));
                log.info("New block template at height {}", lastBlockTemplate.getHeight());
            }
        } catch (Exception e) {
            log.error("Failed to update block template", e);
        }
    }


    @Override
    public void onApplicationEvent(NewNetworkBlockEvent newNetworkBlockEvent) {
        update();
    }
}
