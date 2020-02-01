package uk.offtopica.moneropool.hash;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import uk.offtopica.randomx4j.RandomX;

@Slf4j
class RandomXVM {
    private final int flags;
    @Getter
    private final byte[] key;
    private Pointer dataset;
    private VmPool vmPool;
    @Getter
    private boolean ready;

    public RandomXVM(int flags, byte[] key) {
        this.flags = flags;
        this.key = key;
        ready = false;
        dataset = Pointer.NULL;
        initDataset();
        initVm();
    }

    public byte[] hash(byte[] data) {
        if (!ready) {
            throw new IllegalStateException("Not ready");
        }

        Pointer vm = vmPool.get();
        try {
            return RandomX.calculateHash(vm, data);
        } finally {
            vmPool.release(vm);
        }
    }

    private void initDataset() {
        log.debug("Allocating new RandomX cache");
        Pointer cache = RandomX.allocCache(flags);
        RandomX.initCache(cache, key);

        log.debug("Allocating new RandomX dataset");
        dataset = RandomX.allocDataset(flags);
        long datasetItemCount = RandomX.datasetItemCount().longValue();

        // TODO: Make config value.
        int threadCount = Runtime.getRuntime().availableProcessors();
        log.debug("Initialising randomX dataset with {} threads", threadCount);

        long size = datasetItemCount / threadCount;
        long last = datasetItemCount % threadCount;
        int start = 0;

        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final long thisSize = size + (i == threadCount - 1 ? last : 0);
            final int thisStart = start;

            threads[i] = new Thread(() -> {
                RandomX.initDataset(dataset, cache, new NativeLong(thisStart), new NativeLong(thisSize));
            });
            start += thisSize;
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                log.error("Interrupted when initialising dataset", e);
            }
        }

        log.debug("Dataset initialisation finished");
    }

    private void initVm() {
        vmPool = new VmPool(flags, 8, dataset);
        ready = true;
    }

    public void destroy() {
        vmPool.shutdown();

        if (!dataset.equals(Pointer.NULL)) {
            RandomX.releaseDataset(dataset);
            dataset = Pointer.NULL;
        }
    }
}
