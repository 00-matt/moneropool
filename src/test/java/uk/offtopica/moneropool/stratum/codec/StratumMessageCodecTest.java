package uk.offtopica.moneropool.stratum.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import uk.offtopica.moneropool.MoneroPoolTest;
import uk.offtopica.moneropool.stratum.message.StratumMessage;
import uk.offtopica.moneropool.stratum.message.StratumMessageCodec;
import uk.offtopica.moneropool.stratum.message.StratumRequest;

import java.util.Map;
import java.util.stream.Stream;

@MoneroPoolTest
class StratumMessageCodecTest {
    @Autowired
    ApplicationContext applicationContext;

    EmbeddedChannel chan;

    static Stream<Arguments> provideValidStratumMessages() {
        return Stream.of(
                Arguments.of(
                        "login",
                        new StratumRequest("0", "login", Map.of("login", "foo", "password", "bar")),
                        "{\"id\":\"0\",\"method\":\"login\",\"params\":{\"password\":\"bar\",\"login\":\"foo\"}}"
                ),
                Arguments.of(
                        "submit",
                        new StratumRequest(1, "submit", Map.of("job_id", "00", "nonce", "00", "result", "00")),
                        "{\"id\":1,\"method\":\"submit\",\"params\":{\"nonce\":\"00\",\"job_id\":\"00\"," +
                                "\"result\":\"00\"}}"
                )
        );
    }

    @BeforeEach
    void setup() {
        chan = new EmbeddedChannel(applicationContext.getBean(StratumMessageCodec.class));
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("uk.offtopica.moneropool.stratum.codec.StratumMessageCodecTest#provideValidStratumMessages")
    void testEncode(String name, StratumMessage message, String json) throws JSONException {
        chan.writeOutbound(message);
        final String encoded = ((ByteBuf) chan.readOutbound()).toString(CharsetUtil.UTF_8);
        JSONAssert.assertEquals(json, encoded, false);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("uk.offtopica.moneropool.stratum.codec.StratumMessageCodecTest#provideValidStratumMessages")
    void testDecodeValid(String name, StratumMessage message, String json) {
        chan.writeInbound(Unpooled.wrappedBuffer(json.getBytes(CharsetUtil.UTF_8)));
        final StratumMessage decoded = chan.readInbound();
        Assertions.assertEquals(message, decoded);
    }
}
