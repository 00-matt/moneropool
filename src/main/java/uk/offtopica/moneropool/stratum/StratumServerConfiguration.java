package uk.offtopica.moneropool.stratum;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.offtopica.moneropool.stratum.message.StratumMessage;
import uk.offtopica.moneropool.stratum.message.StratumMessageDeserializer;

@Configuration
public class StratumServerConfiguration {
    @Autowired
    private StratumChannelInitializer stratumChannelInitializer;

    @Bean
    StratumMessageDeserializer stratumMessageDeserializer() {
        return new StratumMessageDeserializer();
    }

    @Bean
    ObjectMapper objectMapper(StratumMessageDeserializer stratumMessageDeserializer) {
        final ObjectMapper objectMapper = new ObjectMapper();
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(StratumMessage.class, stratumMessageDeserializer);
        objectMapper.registerModule(module);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    EventLoopGroup parentGroup(@Value("${stratum.parentThreads}") int parentThreads) {
        return new NioEventLoopGroup(parentThreads);
    }

    @Bean
    EventLoopGroup childGroup(@Value("${stratum.childThreads}") int childThreads) {
        return new NioEventLoopGroup(childThreads);
    }

    @Bean
    ChannelGroup minerChannelGroup() {
        return new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    @Bean
    Class<? extends ServerChannel> serverChannel() {
        return NioServerSocketChannel.class;
    }

    @Bean
    ServerBootstrap serverBootstrap(@Qualifier("parentGroup") EventLoopGroup parentGroup,
                                    @Qualifier("childGroup") EventLoopGroup childGroup,
                                    Class<? extends ServerChannel> serverChannel,
                                    @Value("${stratum.port}") int port,
                                    @Value("${stratum.backlog}") int backlog) {
        ServerBootstrap b = new ServerBootstrap();
        b.group(parentGroup, childGroup);
        b.option(ChannelOption.SO_BACKLOG, backlog);
        b.channel(serverChannel);
        b.childHandler(stratumChannelInitializer);
        b.childOption(ChannelOption.SO_KEEPALIVE, true);
        return b;
    }
}
