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
    private Pointer vm;
    private boolean ready;

    public RandomXVM(int flags, byte[] key) {
        this.flags = flags;
        this.key = key;
        ready = false;
        dataset = Pointer.NULL;
        vm = Pointer.NULL;
        initDataset();
        initVm();
    }

    public byte[] hash(byte[] data) {
        if (!ready) {
            throw new IllegalStateException("Not ready");
        }

        return RandomX.calculateHash(vm, data);
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
        long last = datasetItemCount / threadCount;
        int start = 0;

        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            long thisSize = size;
            final int thisStart = start;
            if (i == threadCount - 1) {
                thisSize += last;
            }
            threads[i] = new Thread(() -> {
                RandomX.initDataset(dataset, cache, new NativeLong(thisStart), new NativeLong(size));
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
        vm = RandomX.createVm(flags, Pointer.NULL, dataset);
        ready = true;
    }

    public void destroy() {
        if (!vm.equals(Pointer.NULL)) {
            RandomX.destroyVm(vm);
            vm = Pointer.NULL;
        }

        if (!dataset.equals(Pointer.NULL)) {
            RandomX.releaseDataset(dataset);
            dataset = Pointer.NULL;
        }
    }
}
