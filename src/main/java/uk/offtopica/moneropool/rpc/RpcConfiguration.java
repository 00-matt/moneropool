package uk.offtopica.moneropool.rpc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcConfiguration {
    @Bean
    RpcResponseDeserializer moneroRpcResponseDeserializer() {
        return new RpcResponseDeserializer();
    }
}
