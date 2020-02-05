package uk.offtopica.moneropool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.offtopica.addressutil.InvalidAddressException;
import uk.offtopica.addressutil.monero.MoneroAddress;
import uk.offtopica.addressutil.monero.MoneroAddressDecoder;
import uk.offtopica.addressutil.monero.MoneroNetworkConstants;

@Component
public class AddressValidator {
    private final boolean allowIntegratedAddress;
    private final MoneroAddressDecoder decoder;

    public AddressValidator(@Value("${pool.coin}") String coin,
                            @Value("${pool.network}") String network,
                            @Value("${payments.allowIntegrated}") boolean allowIntegratedAddress) {
        this.allowIntegratedAddress = false;
        decoder = new MoneroAddressDecoder(getNetworkConstants(coin, network));
    }

    public boolean validate(String address) {
        try {
            MoneroAddress decoded = decoder.decode(address);
            return allowIntegratedAddress || !decoded.isIntegratedAddress();
        } catch (InvalidAddressException e) {
            return false;
        }
    }

    private static MoneroNetworkConstants getNetworkConstants(String coin, String network) {
        if (!coin.equals("monero")) {
            throw new IllegalArgumentException("Unknown coin " + coin);
        }

        switch (network) {
            case "mainnet":
                return MoneroNetworkConstants.MAINNET;
            case "testnet":
                return MoneroNetworkConstants.TESTNET;
            case "stagenet":
                return MoneroNetworkConstants.STAGENET;
            default:
                throw new IllegalArgumentException("Unknown network " + network);
        }
    }
}
