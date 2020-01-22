package uk.offtopica.moneropool;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.offtopica.moneropool.rpc.MoneroDaemon;

import java.util.Arrays;

@Component
@Slf4j
public class BlockTemplateNotifier {
    private static final int RESERVE_SIZE = 60;

    @Autowired
    private MoneroDaemon moneroDaemon;

    @Autowired
    @Qualifier("minerChannelGroup")
    private ChannelGroup minerChannelGroup;

    @Value("${blocktemplate.address}")
    private String walletAddress;

    // TODO: This should probably be an atomic reference.
    @Getter
    private BlockTemplate lastBlockTemplate;

    public void update() {
        log.trace("Updating block template");
        try {
            BlockTemplate candidate = moneroDaemon.getBlockTemplate(walletAddress, RESERVE_SIZE).get();
            if (lastBlockTemplate == null || !Arrays.equals(lastBlockTemplate.getPrevHash(), candidate.getPrevHash())) {
                lastBlockTemplate = candidate;
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