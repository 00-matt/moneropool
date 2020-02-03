package uk.offtopica.moneropool.stratum;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
@Slf4j
public class StratumServer {
    @Value("${stratum.port}")
    private int port;

    @Autowired
    @Qualifier("stratumParentGroup")
    private EventLoopGroup parentGroup;

    @Autowired
    @Qualifier("stratumChildGroup")
    private EventLoopGroup childGroup;

    @Autowired
    private ServerBootstrap serverBootstrap;

    public ChannelFuture start() {
        log.info("Going to listen on :{}", port);
        return serverBootstrap.bind(port);
    }

    @PreDestroy
    public void stop() {
        childGroup.shutdownGracefully();
        parentGroup.shutdownGracefully();
    }
}
