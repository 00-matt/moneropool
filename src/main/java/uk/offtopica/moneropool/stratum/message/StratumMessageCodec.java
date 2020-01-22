package uk.offtopica.moneropool.stratum.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class StratumMessageCodec extends ByteToMessageCodec<Object> {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        DataOutput outputStream = new ByteBufOutputStream(out);
        objectMapper.writeValue(outputStream, msg);
        outputStream.write('\n');
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        DataInput inputStream = new ByteBufInputStream(in);
        out.add(objectMapper.readValue(inputStream, StratumMessage.class));
    }
}
