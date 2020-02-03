package uk.offtopica.moneropool.stratum;

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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.offtopica.moneropool.stratum.message.StratumMessage;
import uk.offtopica.moneropool.stratum.message.StratumMessageDeserializer;

@Configuration
public class StratumServerConfiguration {
    @Bean
    SimpleModule stratumJacksonModule() {
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(StratumMessage.class, new StratumMessageDeserializer());
        return module;
    }

    @Bean
    EventLoopGroup stratumParentGroup(@Value("${stratum.parentThreads}") int parentThreads) {
        return new NioEventLoopGroup(parentThreads);
    }

    @Bean
    EventLoopGroup stratumChildGroup(@Value("${stratum.childThreads}") int childThreads) {
        return new NioEventLoopGroup(childThreads);
    }

    @Bean
    ChannelGroup minerChannelGroup() {
        return new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    @Bean
    Class<? extends ServerChannel> stratumServerChannel() {
        return NioServerSocketChannel.class;
    }

    @Bean
    ServerBootstrap stratumServerBootstrap(@Qualifier("stratumParentGroup") EventLoopGroup parentGroup,
                                           @Qualifier("stratumChildGroup") EventLoopGroup childGroup,
                                           @Qualifier("stratumServerChannel") Class<? extends ServerChannel> serverChannel,
                                           @Value("${stratum.backlog}") int backlog,
                                           StratumChannelInitializer stratumChannelInitializer) {
        ServerBootstrap b = new ServerBootstrap();
        b.group(parentGroup, childGroup);
        b.option(ChannelOption.SO_BACKLOG, backlog);
        b.channel(serverChannel);
        b.childHandler(stratumChannelInitializer);
        b.childOption(ChannelOption.SO_KEEPALIVE, true);
        return b;
    }
}
