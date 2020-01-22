package uk.offtopica.moneropool.rpc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.offtopica.moneropool.BlockTemplate;
import uk.offtopica.moneropool.Difficulty;
import uk.offtopica.moneropool.util.HexUtils;
import uk.offtopica.moneropool.util.UncheckedObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static uk.offtopica.moneropool.util.HexUtils.hexStringToByteArray;

@Component
public class MoneroDaemon {
    @Value("${daemon.address}")
    private String address;

    @Autowired
    private UncheckedObjectMapper objectMapper;

    public CompletableFuture<BlockTemplate> getBlockTemplate(String address, int reserveSize) throws IOException {
        return jsonrpc("get_block_template", Map.of("wallet_address", address, "reserve_size", reserveSize))
                .thenApply(response -> {
                    // TODO: Check for error instead of violently crashing.
                    // TODO: Use ObjectMapper instead of untyped Map.
                    final Map<String, Object> result = (Map<String, Object>) response.get("result");
                    BlockTemplate blockTemplate = new BlockTemplate();
                    blockTemplate.setHashingBlob(hexStringToByteArray((String) result.get("blockhashing_blob")));
                    blockTemplate.setTemplateBlob(hexStringToByteArray((String) result.get("blocktemplate_blob")));
                    // TODO: Wide difficulty.
                    blockTemplate.setDifficulty(new Difficulty(Long.valueOf((Integer) result.get("difficulty"))));
                    blockTemplate.setExpectedReward((Long) result.get("expected_reward"));
                    blockTemplate.setHeight(Long.valueOf((Integer) result.get("height")));
                    blockTemplate.setPrevHash(hexStringToByteArray((String) result.get("prev_hash")));
                    blockTemplate.setReservedOffset((Integer) result.get("reserved_offset"));
                    blockTemplate.setSeedHeight(Long.valueOf((Integer) result.get("seed_height")));
                    blockTemplate.setSeedHash(hexStringToByteArray((String) result.get("seed_hash")));
                    return blockTemplate;
                });
    }

    public CompletableFuture<Boolean> submitBlock(byte[] blob) throws IOException {
        String hex = HexUtils.byteArrayToHexString(blob);
        // TODO: Check for error instead of always returning true.
        return jsonrpc("submit_block", List.of(hex)).thenApply(result -> true);
    }

    private CompletableFuture<Map<String, Object>> jsonrpc(String method, Object params)
            throws IOException {
        return request(URI.create(address), Map.of("id", "0", "jsonrpc", "2.0", "method", method, "params", params));
    }

    private CompletableFuture<Map<String, Object>> request(URI uri, Map<String, Object> params) throws IOException {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(params)))
                .header("Accept", "application/json")
                .build();

        return HttpClient.newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(objectMapper::readValue);
    }
}
