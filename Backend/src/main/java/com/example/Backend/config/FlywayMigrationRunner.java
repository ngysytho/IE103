package com.example.Backend.config;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
public class FlywayMigrationRunner {
    private static final Logger log = LoggerFactory.getLogger(FlywayMigrationRunner.class);

    @Bean
    ApplicationRunner runFlywayMigrations(DataSource dataSource, Environment environment) {
        return args -> {
            String[] locations = split(
                    environment.getProperty("spring.flyway.locations", "classpath:db/migration")
            );
            String defaultSchema = environment.getProperty("spring.flyway.default-schema", "public");
            String[] schemas = split(environment.getProperty("spring.flyway.schemas", defaultSchema));
            boolean baselineOnMigrate = environment.getProperty(
                    "spring.flyway.baseline-on-migrate",
                    Boolean.class,
                    true
            );

            log.info(
                    "Running Flyway migrations manually: locations={}, schemas={}",
                    Arrays.toString(locations),
                    Arrays.toString(schemas)
            );

            Flyway.configure()
                    .dataSource(dataSource)
                    .locations(locations)
                    .defaultSchema(defaultSchema)
                    .schemas(schemas)
                    .baselineOnMigrate(baselineOnMigrate)
                    .load()
                    .migrate();
        };
    }

    private String[] split(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toArray(String[]::new);
    }
}
