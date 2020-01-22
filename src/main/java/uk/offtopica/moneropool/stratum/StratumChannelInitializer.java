package uk.offtopica.moneropool.stratum;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import uk.offtopica.moneropool.stratum.message.StratumMessageCodec;

@Component
public class StratumChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        final ChannelPipeline p = ch.pipeline();

        p.addLast(new LineBasedFrameDecoder(1024, true, true));
        p.addLast(applicationContext.getBean(StratumMessageCodec.class));
        p.addLast(new IdleStateHandler(0, 0, 300)); // TODO: Extract to config value?
        p.addLast(applicationContext.getBean(StratumServerHandler.class));
    }
}
