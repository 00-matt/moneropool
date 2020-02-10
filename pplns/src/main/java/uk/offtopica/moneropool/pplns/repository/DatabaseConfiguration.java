package uk.offtopica.moneropool.pplns.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sql2o.Sql2o;

@Configuration
public class DatabaseConfiguration {
    @Bean
    Sql2o sql2o(@Value("${database.url}") String url,
                @Value("${database.user}") String user,
                @Value("${database.pass}") String pass) {
        return new Sql2o(url, user, pass);
    }
}
