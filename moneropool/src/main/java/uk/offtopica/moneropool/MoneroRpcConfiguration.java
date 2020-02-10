package uk.offtopica.moneropool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.offtopica.monerorpc.daemon.MoneroDaemonRpcClient;

import java.net.URI;

@Configuration
public class MoneroRpcConfiguration {
    @Bean
    MoneroDaemonRpcClient moneroDaemonRpcClient(@Value("${daemon.address}") URI daemonAddress) {
        return new MoneroDaemonRpcClient(daemonAddress);
    }
}
