package uk.offtopica.moneropool;

import io.netty.channel.ChannelFuture;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import uk.offtopica.moneropool.stratum.StratumServer;

import java.util.concurrent.ThreadLocalRandom;

@Configuration
@PropertySource("classpath:application.properties")
public class MoneroPoolApplication {
    public static void main(String[] args) throws Exception {
        final ConfigurableApplicationContext context =
                new AnnotationConfigApplicationContext("uk.offtopica.moneropool");

        // Get an initial block template.
        final BlockTemplateNotifier blockTemplateNotifier = context.getBean(BlockTemplateNotifier.class);
        blockTemplateNotifier.update();

        final NotifyServer notifyServer = context.getBean(NotifyServer.class);
        notifyServer.start();

        final StratumServer stratumServer = context.getBean(StratumServer.class);
        final ChannelFuture f = stratumServer.start().sync();
        f.channel().closeFuture().sync();
        context.close();
    }

    @Bean
    InstanceId instanceId() {
        return new InstanceId(ThreadLocalRandom.current().nextLong());
    }
}
