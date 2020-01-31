package uk.offtopica.moneropool.rpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.offtopica.moneropool.BlockTemplate;
import uk.offtopica.moneropool.util.HexUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Component
@Slf4j
public class MoneroDaemon {
    @Value("${daemon.address}")
    private String address;

    @Autowired
    private ObjectMapper objectMapper;

    public CompletableFuture<BlockTemplate> getBlockTemplate(String address, int reserveSize) throws IOException {
        return request("get_block_template", new DaemonGetBlockTemplateRequest(address, reserveSize))
                .thenApply(response -> {
                    if (response.getError() != null) {
                        log.error("get_block_template error: {}", response.getError());
                        throw new RuntimeException();
                    }
                    return ((DaemonGetBlockTemplateResult) response.getResult()).asBlockTemplate();
                });
    }

    public CompletableFuture<Boolean> submitBlock(byte[] blob) throws IOException {
        String hex = HexUtils.byteArrayToHexString(blob);
        return request("submit_block", List.of(hex)).thenApply(result -> {
            if (result.getError() != null) {
                log.error("submit_block: {}", result.getError());
                return false;
            }
            return true;
        });
    }

    private CompletableFuture<RpcResponse> request(String method, Object params) throws IOException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(address))
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(
                        Map.of(
                                "id", "0",
                                "method", method,
                                "params", params
                        )
                )))
                .header("Accept", "application/json")
                .build();

        return HttpClient.newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(body -> {
                    try {
                        return objectMapper.readValue(body, RpcResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new CompletionException(e);
                    }
                });
    }
}
