package uk.offtopica.moneropool.notify;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class NotifyClient {
    @Autowired
    @Qualifier("redisClientBootstrap")
    private Bootstrap redisClientBootstrap;

    @Autowired
    @Qualifier("redisEventLoopGroup")
    private EventLoopGroup redisEventLoopGroup;

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    public ChannelFuture start() {
        return redisClientBootstrap.connect(redisHost, redisPort);
    }

    @PreDestroy
    public void stop() {
        redisEventLoopGroup.shutdownGracefully();
    }
}
