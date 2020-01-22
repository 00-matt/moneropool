package uk.offtopica.moneropool.stratum;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.offtopica.moneropool.*;
import uk.offtopica.moneropool.rpc.MoneroDaemon;
import uk.offtopica.moneropool.stratum.message.StratumError;
import uk.offtopica.moneropool.stratum.message.StratumRequest;
import uk.offtopica.moneropool.stratum.message.StratumResponse;
import uk.offtopica.moneropool.util.HexUtils;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StratumServerHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private MoneroDaemon daemon;

    @Autowired
    @Qualifier("minerChannelGroup")
    private ChannelGroup minerChannelGroup;

    @Autowired
    private BlockTemplateNotifier blockTemplateNotifier;

    @Autowired
    private JobFactory jobFactory;

    private SocketAddress remoteAddress;
    private BlockTemplate blockTemplate;
    private Miner miner;
    private Job lastJob;

    private void onLogin(ChannelHandlerContext ctx, StratumRequest request) {
        if (miner != null) {
            replyWithError(ctx, request.getId(), new StratumError(-1, "Already logged in"));
        }

        Map<String, Object> params = request.getParams();
        final String username = (String) params.get("login");
        final String password = (String) params.get("pass");
        final String agent;
        if (params.containsKey("agent")) {
            agent = (String) params.get("agent");
            // TODO: Check for xmr-node-proxy (tell them to go away).
        } else {
            agent = null;
        }
        miner = new Miner();
        miner.setUsername(username);
        miner.setPassword(password);
        miner.setAgent(agent);
        miner.setId(ThreadLocalRandom.current().nextLong());

        minerChannelGroup.add(ctx.channel());
        blockTemplate = blockTemplateNotifier.getLastBlockTemplate();

        jobFactory.setBlockTemplate(blockTemplate);
        lastJob = jobFactory.getJob();

        reply(ctx, request.getId(), Map.of(
                "id", miner.getId().toString(),
                "status", "OK",
                "job", Map.of(
                        "blob", HexUtils.byteArrayToHexString(lastJob.getBlob()),
                        "job_id", lastJob.getId().toString(),
                        "target", lastJob.getDifficulty().getHex(),
                        "id", miner.getId().toString(),
                        "seed_hash", HexUtils.byteArrayToHexString(lastJob.getSeedHash()),
                        "height", lastJob.getHeight().toString()
                )
        ));
    }

    private void onNewBlockTemplate(ChannelHandlerContext ctx) {
        blockTemplate = blockTemplateNotifier.getLastBlockTemplate();

        jobFactory.setBlockTemplate(blockTemplate);
        lastJob = jobFactory.getJob();

        sendNotification(ctx, "job", Map.of(
                "blob", HexUtils.byteArrayToHexString(lastJob.getBlob()),
                "job_id", lastJob.getId().toString(),
                "target", lastJob.getDifficulty().getHex(),
                "id", miner.getId().toString(),
                "seed_hash", HexUtils.byteArrayToHexString(lastJob.getSeedHash()),
                "height", lastJob.getHeight().toString()
        ));
    }

    private void onSubmit(ChannelHandlerContext ctx, StratumRequest request) {
        log.info("{}", request);
        final byte[] nonce = HexUtils.hexStringToByteArray((String) request.getParams().get("nonce"));
        final byte[] result = HexUtils.hexStringToByteArray((String) request.getParams().get("result"));

        final Difficulty shareDifficulty = Difficulty.ofShare(result);

        if (shareDifficulty.compareTo(lastJob.getDifficulty()) < 0) {
            replyWithError(ctx, request.getId(), new StratumError(-1, "Low difficulty share"));
        }

        // TODO: Extract this elsewhere.
        final byte[] blockBlob = lastJob.getTemplate().getTemplateBlob().clone();
        // TODO: Use System.arrayCopy.
        blockBlob[39] = nonce[0];
        blockBlob[40] = nonce[1];
        blockBlob[41] = nonce[2];
        blockBlob[42] = nonce[3];

        // TODO: Validate result hash.

        if (shareDifficulty.compareTo(lastJob.getTemplate().getDifficulty()) >= 0) {
            log.info("omg a block");

            // TODO: Extract elsewhere.
            // TODO: Use executor.
            new Thread(() -> {
                try {
                    daemon.submitBlock(blockBlob);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        reply(ctx, request.getId(), Map.of("status", "OK"));
    }

    private void onUnknownRequest(ChannelHandlerContext ctx, StratumRequest request) {
        replyWithError(ctx, request.getId(), new StratumError(-1, "Unknown method"));
    }

    private void reply(ChannelHandlerContext ctx, Object id, Map<String, Object> result) {
        final StratumResponse response = new StratumResponse();
        response.setId(id);
        response.setResult(result);
        ctx.writeAndFlush(response);
    }

    private void sendNotification(ChannelHandlerContext ctx, String method, Map<String, Object> params) {
        final StratumRequest request = new StratumRequest();
        request.setMethod(method);
        request.setParams(params);
        ctx.writeAndFlush(request);
    }

    private void replyWithError(ChannelHandlerContext ctx, Object id, StratumError error) {
        final StratumResponse response = new StratumResponse();
        response.setId(id);
        response.setError(error);
        ctx.writeAndFlush(response);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        remoteAddress = ctx.channel().remoteAddress();
        log.info("New connection from {}", remoteAddress);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        if (obj instanceof StratumRequest) {
            final StratumRequest request = (StratumRequest) obj;

            switch (request.getMethod()) {
                case "login":
                    onLogin(ctx, request);
                    break;
                case "submit":
                    onSubmit(ctx, request);
                    break;
                default: {
                    onUnknownRequest(ctx, request);
                    break;
                }
            }
        } else if (obj instanceof StratumResponse) {
            final StratumResponse response = (StratumResponse) obj;
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // TODO: Detect miner/pool idle differently, and send a keepalive/ping message?
            ctx.close();
        } else if (evt instanceof NewBlockTemplateEvent) {
            onNewBlockTemplate(ctx);
        } else {
            log.warn("Unknown event {}", evt.getClass().getSimpleName());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        minerChannelGroup.remove(ctx.channel());
        if (log.isInfoEnabled()) {
            if (miner == null) {
                log.info("{} disconnected", remoteAddress);
            } else {
                log.info("{}/{} disconnected", remoteAddress, miner.getUsername());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof TooLongFrameException) {
            // TODO: Temporary ban?
            ctx.close();
            log.warn("Flood detected from {}", ctx.channel().remoteAddress());
            return;
        }

        log.error("Unhandled exception in handler", cause);
        ctx.close();
    }
}