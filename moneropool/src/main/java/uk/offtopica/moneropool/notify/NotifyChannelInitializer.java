package uk.offtopica.moneropool.notify;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class NotifyChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        final ChannelPipeline p = ch.pipeline();
        p.addLast(new RedisDecoder());
        p.addLast(new RedisBulkStringAggregator());
        p.addLast(new RedisArrayAggregator());
        p.addLast(new RedisEncoder());
        p.addLast(applicationContext.getBean(NotifyClientHandler.class));
    }
}
