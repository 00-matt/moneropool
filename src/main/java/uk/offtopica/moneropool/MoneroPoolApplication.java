package uk.offtopica.moneropool;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.netty.channel.ChannelFuture;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import uk.offtopica.moneropool.rpc.RpcResponse;
import uk.offtopica.moneropool.rpc.RpcResponseDeserializer;
import uk.offtopica.moneropool.stratum.StratumServer;
import uk.offtopica.moneropool.stratum.message.StratumMessage;
import uk.offtopica.moneropool.stratum.message.StratumMessageDeserializer;

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

    @Bean
    ObjectMapper objectMapper(StratumMessageDeserializer stratumMessageDeserializer,
                              RpcResponseDeserializer rpcResponseDeserializer) {
        final ObjectMapper objectMapper = new ObjectMapper();
        // TODO:
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(StratumMessage.class, stratumMessageDeserializer);
        module.addDeserializer(RpcResponse.class, rpcResponseDeserializer);
        objectMapper.registerModule(module);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
