package uk.offtopica.moneropool;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class JacksonConfiguration {
    @Bean
    ObjectMapper objectMapper(List<SimpleModule> modules) {
        final ObjectMapper objectMapper = new ObjectMapper();
        modules.forEach(objectMapper::registerModule);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
