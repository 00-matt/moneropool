package uk.offtopica.moneropool.pplns.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.offtopica.moneropool.pplns.model.Block;
import uk.offtopica.monerorpc.daemon.MoneroDaemonRpcClient;

@Component
@Slf4j
public class DaemonService {
    private final MoneroDaemonRpcClient rpc;

    @Autowired
    public DaemonService(MoneroDaemonRpcClient daemonRpc) {
        this.rpc = daemonRpc;
    }

    public long getBlockHeight() {
        try {
            return rpc.getBlockCount().get();
        } catch (Exception e) {
            log.error("Failed to get block height", e);
            throw new RuntimeException(e);
        }
    }

    public boolean isOrphan(Block block) {
        // TODO:
        return false;
    }
}
