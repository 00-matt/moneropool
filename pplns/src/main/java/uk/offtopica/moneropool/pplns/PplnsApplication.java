package uk.offtopica.moneropool.pplns;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import uk.offtopica.moneropool.pplns.service.PplnsService;
import uk.offtopica.monerorpc.daemon.MoneroDaemonRpcClient;
import uk.offtopica.monerorpc.wallet.MoneroWalletRpcClient;

import java.net.URI;

@Configuration
@PropertySource("classpath:application.properties")
@Slf4j
public class PplnsApplication {
    public static void main(String[] args) {
        try (final ConfigurableApplicationContext context =
                     new AnnotationConfigApplicationContext("uk.offtopica.moneropool.pplns")) {
            final PplnsService pplnsService = context.getBean(PplnsService.class);
            pplnsService.processAllPayments();
        }
    }

    @Bean
    MoneroDaemonRpcClient daemonRpcClient(@Value("${daemon.address}") URI uri) {
        return new MoneroDaemonRpcClient(uri);
    }
}
