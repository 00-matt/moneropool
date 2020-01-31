package uk.offtopica.moneropool;

import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SpringJUnitConfig(classes = TestApplicationConfiguration.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface MoneroPoolTest {
}
