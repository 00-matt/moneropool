package uk.offtopica.moneropool;

import io.netty.channel.ChannelFuture;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import uk.offtopica.moneropool.stratum.StratumServer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Configuration
@PropertySource("classpath:application.properties")
public class MoneroPoolApplication {
    public static void main(String[] args) throws Exception {
        final ConfigurableApplicationContext context =
                new AnnotationConfigApplicationContext("uk.offtopica.moneropool");

        // TODO: Don't poll.
        final BlockTemplateNotifier blockTemplateNotifier = context.getBean(BlockTemplateNotifier.class);
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleWithFixedDelay(blockTemplateNotifier::update, 0, 5, TimeUnit.SECONDS);

        final StratumServer stratum = context.getBean(StratumServer.class);
        final ChannelFuture f = stratum.start().sync();
        f.channel().closeFuture().sync();
        context.close();
    }

    @Bean
    InstanceId instanceId() {
        return new InstanceId(ThreadLocalRandom.current().nextLong());
    }
}
