package uk.offtopica.moneropool.notify;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.InlineCommandRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.offtopica.moneropool.BlockTemplateNotifier;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class NotifyClientHandler extends ChannelInboundHandlerAdapter {
    private static final ByteBuf MESSAGE = Unpooled.copiedBuffer("message", StandardCharsets.UTF_8);

    @Autowired
    private BlockTemplateNotifier blockTemplateNotifier;

    @Value("${pool.coin}")
    private String coin;

    @Value("${pool.network}")
    private String network;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new InlineCommandRedisMessage(String.format("SUBSCRIBE %s.%s.blocks", coin, network)));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof ArrayRedisMessage) {
                ArrayRedisMessage arr = (ArrayRedisMessage) msg;
                List<RedisMessage> elems = arr.children();

                if (elems.size() != 3) {
                    return;
                }

                if (!(elems.get(0) instanceof FullBulkStringRedisMessage)) {
                    return;
                }

                if (!ByteBufUtil.equals(MESSAGE, ((FullBulkStringRedisMessage) elems.get(0)).content())) {
                    return;
                }

                // No need to check the channel (elems[1]), as we've only subscribed to one.

                if (!(elems.get(2) instanceof FullBulkStringRedisMessage)) {
                    return;
                }

                final String hash = ((FullBulkStringRedisMessage) elems.get(2)).content().toString(CharsetUtil.UTF_8);

                log.trace("Notified of block hash {}", hash);

                blockTemplateNotifier.update();
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
