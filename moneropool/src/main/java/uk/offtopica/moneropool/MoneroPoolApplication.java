package uk.offtopica.moneropool;

import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import uk.offtopica.moneropool.notify.NotifyClient;
import uk.offtopica.moneropool.stratum.StratumServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

        final NotifyClient notifyClient = context.getBean(NotifyClient.class);
        notifyClient.start();

        final StratumServer stratumServer = context.getBean(StratumServer.class);
        final ChannelFuture f = stratumServer.start().sync();
        f.channel().closeFuture().sync();
        context.close();
    }

    @Bean
    InstanceId instanceId() {
        return new InstanceId(ThreadLocalRandom.current().nextLong());
    }

    @Bean
    ExecutorService globalExecutor(@Value("${globalExecutor.threads}") int threads) {
        return Executors.newFixedThreadPool(threads);
    }
}
