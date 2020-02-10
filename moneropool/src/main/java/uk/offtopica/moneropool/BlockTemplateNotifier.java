package uk.offtopica.moneropool;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.offtopica.moneropool.hash.ResultHashValidator;
import uk.offtopica.monerorpc.daemon.BlockTemplate;
import uk.offtopica.monerorpc.daemon.MoneroDaemonRpcClient;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;

@Component
@Slf4j
public class BlockTemplateNotifier {
    private static final int RESERVE_SIZE = 20;

    @Autowired
    private MoneroDaemonRpcClient moneroDaemon;

    @Autowired
    @Qualifier("minerChannelGroup")
    private ChannelGroup minerChannelGroup;

    @Autowired
    private ResultHashValidator resultHashValidator;

    @Value("${blocktemplate.address}")
    private String walletAddress;

    @Getter
    private BlockTemplate lastBlockTemplate;

    @Autowired
    @Qualifier("globalExecutor")
    private ExecutorService globalExecutor;

    public void update() {
        log.trace("Updating block template");
        try {
            BlockTemplate candidate = moneroDaemon.getBlockTemplate(walletAddress, RESERVE_SIZE).get();
            if (lastBlockTemplate == null || !Arrays.equals(lastBlockTemplate.getPrevHash(), candidate.getPrevHash())) {
                lastBlockTemplate = candidate;
                globalExecutor.execute(() -> resultHashValidator.onBlockTemplate(lastBlockTemplate));
                final NewBlockTemplateEvent event = new NewBlockTemplateEvent();
                for (Channel c : minerChannelGroup) {
                    c.pipeline().fireUserEventTriggered(event);
                }
                log.info("New block template at height {}", lastBlockTemplate.getHeight());
            }
        } catch (Exception e) {
            log.error("Failed to update block template", e);
        }
    }
}
