package uk.offtopica.moneropool.hash;

import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import uk.offtopica.randomx4j.RandomX;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
class VmPool {
    private BlockingQueue<Pointer> vms;
    private boolean shutdown;

    public VmPool(int flags, int size, Pointer dataset) {
        vms = new LinkedBlockingQueue<>(size);
        for (int i = 0; i < size; i++) {
            vms.add(RandomX.createVm(flags, Pointer.NULL, dataset));
        }
        shutdown = false;
    }

    public Pointer get() {
        try {
            return vms.take();
        } catch (InterruptedException e) {
            log.error("Interrupted", e);
            return null;
        }
    }

    public void release(Pointer vm) {
        if (shutdown) {
            RandomX.destroyVm(vm);
        } else {
            vms.offer(vm);
        }
    }

    public void shutdown() {
        shutdown = true;
        List<Pointer> drain = new ArrayList<>();
        vms.drainTo(drain);
        for (Pointer vm : drain) {
            RandomX.destroyVm(vm);
        }
    }
}
