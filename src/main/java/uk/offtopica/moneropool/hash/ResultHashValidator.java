package uk.offtopica.moneropool.hash;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.offtopica.moneropool.BlockTemplate;
import uk.offtopica.moneropool.util.ArrayUtils;
import uk.offtopica.randomx4j.RandomX;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
@Slf4j
public class ResultHashValidator {
    private int flags;
    private RandomXVM currentVm;

    @Autowired
    @Qualifier("globalExecutor")
    private ExecutorService globalExecutor;

    public ResultHashValidator() {
        flags = RandomX.getFlags();
        flags |= 4; // RANDOMX_FLAG_FULL_MEM
    }

    public CompletableFuture<Boolean> validate(byte[] candidate, byte[] data) {
        if (currentVm == null) {
            log.error("Tried to validate hash, but VM was null");
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            byte[] actual = ArrayUtils.reverse(currentVm.hash(data));
            return Arrays.equals(candidate, actual);
        }, globalExecutor);
    }

    public void onBlockTemplate(BlockTemplate blockTemplate) {
        if (currentVm == null || !Arrays.equals(currentVm.getKey(), blockTemplate.getSeedHash())) {
            log.trace("Changing currentVm");

            if (currentVm != null) {
                currentVm.destroy();
                currentVm = null;
            }

            currentVm = new RandomXVM(flags, blockTemplate.getSeedHash());
        }
    }
}
