package uk.offtopica.moneropool.database;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sql2o.Sql2o;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {
    @Bean
    DataSource dataSource(@Value("${database.url}") String url,
                          @Value("${database.user}") String user,
                          @Value("${database.pass}") String pass) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(pass);
        return dataSource;
    }

    @Bean
    Sql2o sql2o(DataSource dataSource) {
        return new Sql2o(dataSource);
    }
}
