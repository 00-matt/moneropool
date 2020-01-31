package uk.offtopica.moneropool.rpc;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcConfiguration {
    @Bean
    SimpleModule rpcJacksonModule() {
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(RpcResponse.class, new RpcResponseDeserializer());
        return module;
    }
}
