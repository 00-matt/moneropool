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
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
@Slf4j
public class ResultHashValidator {
    private int flags;
    private RandomXVM currentVm;
    private ReentrantReadWriteLock lock;

    @Autowired
    @Qualifier("globalExecutor")
    private ExecutorService globalExecutor;

    public ResultHashValidator() {
        flags = RandomX.getFlags();
        flags |= 4; // RANDOMX_FLAG_FULL_MEM
        currentVm = null;
        lock = new ReentrantReadWriteLock(true);
    }

    public CompletableFuture<Boolean> validate(byte[] candidate, byte[] data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                lock.readLock().lock();

                if (currentVm == null || !currentVm.isReady()) {
                    log.error("Tried to validate hash, but VM was null or not ready");
                    return false;
                }

                byte[] actual = ArrayUtils.reverse(currentVm.hash(data));

                return Arrays.equals(candidate, actual);
            } finally {
                lock.readLock().unlock();
            }
        }, globalExecutor);
    }

    public void onBlockTemplate(BlockTemplate blockTemplate) {
        try {
            lock.writeLock().lock();

            if (currentVm == null || !Arrays.equals(currentVm.getKey(), blockTemplate.getSeedHash())) {
                log.trace("Changing currentVm");

                if (currentVm != null) {
                    currentVm.destroy();
                    currentVm = null;
                }

                currentVm = new RandomXVM(flags, blockTemplate.getSeedHash());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
