package uk.offtopica.moneropool.notify;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotifyConfiguration {
    @Bean
    EventLoopGroup redisEventLoopGroup() {
        return new NioEventLoopGroup(1);
    }

    @Bean
    Class<? extends SocketChannel> redisSocketChannel() {
        return NioSocketChannel.class;
    }

    @Bean
    Bootstrap redisClientBootstrap(@Qualifier("redisEventLoopGroup") EventLoopGroup group,
                                   @Qualifier("redisSocketChannel") Class<? extends SocketChannel> channel,
                                   NotifyChannelInitializer notifyChannelInitializer) {
        Bootstrap b = new Bootstrap();
        b.group(group);
        b.channel(channel);
        b.handler(notifyChannelInitializer);
        return b;
    }
}
