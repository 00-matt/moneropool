package uk.offtopica.moneropool.pplns.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.offtopica.monerorpc.wallet.MoneroWalletRpcClient;
import uk.offtopica.monerorpc.wallet.TransferDestination;
import uk.offtopica.monerorpc.wallet.TransferResult;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class WalletService {
    private final MoneroWalletRpcClient rpc;

    @Autowired
    public WalletService(MoneroWalletRpcClient rpc) {
        this.rpc = rpc;
    }

    public List<TransferResult> sendPayment(List<TransferDestination> destinations) {
        for (TransferDestination destination : destinations) {
            log.info("Paying {} moneroj to {}", destination.getAmount() / 1e12, destination.getAddress());
        }

        try {
            return rpc.transfer(destinations).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to transfer", e);
            throw new RuntimeException(e);
        }
    }
}
