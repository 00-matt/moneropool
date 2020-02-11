package uk.offtopica.moneropool.stratum;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import uk.offtopica.moneropool.NewBlockTemplateEvent;

@Component
public class StratumBlockTemplateListener implements ApplicationListener<NewBlockTemplateEvent> {
    @Autowired
    @Qualifier("minerChannelGroup")
    private ChannelGroup minerChannelGroup;

    @Override
    public void onApplicationEvent(NewBlockTemplateEvent event) {
        for (Channel c : minerChannelGroup) {
            c.pipeline().fireUserEventTriggered(event);
        }
    }
}
