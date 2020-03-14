package uk.offtopica.moneropool.hash;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import static uk.offtopica.moneropool.util.HexUtils.byteArrayToHexString;

@Slf4j
@RequiredArgsConstructor
public class RandomXService {
    @NonNull
    private final URI uri;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Re-key the RandomX cache.
     *
     * @param seed The new seed.
     * @return A future that when complete, resolves to a boolean that indicates
     * if the operation was successful or not.
     */
    public CompletableFuture<Boolean> seed(byte[] seed) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri.resolve("seed"))
                .POST(HttpRequest.BodyPublishers.ofByteArray(seed))
                .header("Content-Type", "application/x.randomx+bin")
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenApply(response -> response.statusCode() == 204);
    }

    /**
     * Compute the hash of some bytes.
     *
     * @param input The bytes to hash
     * @param seed  RandomX seed hash. May be null
     * @return A future, that when complete, contains the hash of the input
     */
    public CompletableFuture<byte[]> hash(byte[] input, byte[] seed) {
        var requestBuilder = HttpRequest.newBuilder()
                .uri(uri.resolve("hash"))
                .POST(HttpRequest.BodyPublishers.ofByteArray(input))
                .header("Accept", "application/x.randomx+bin")
                .header("Content-Type", "application/x.randomx+bin");

        if (seed != null) {
            requestBuilder.header("RandomX-Seed", byteArrayToHexString(seed));
        }

        return httpClient.sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofByteArray())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        return null;
                    }
                    return response.body();
                });
    }

    /**
     * Compute the hash of some bytes. Does not validate the seed hash.
     *
     * @param input The bytes to hash
     * @return A future, that when complete, contains the hash of the input
     * @see RandomXService#hash(byte[], byte[])
     */
    public CompletableFuture<byte[]> hash(byte[] input) {
        return hash(input, null);
    }
}
