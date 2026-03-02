package com.thecookiezen.archiledger.infrastructure.config;

import com.ladybugdb.Database;
import com.thecookiezen.ladybugdb.spring.config.EnableLadybugDBRepositories;
import com.thecookiezen.ladybugdb.spring.connection.LadybugDBConnectionFactory;
import com.thecookiezen.ladybugdb.spring.connection.PooledConnectionFactory;
import com.thecookiezen.ladybugdb.spring.core.LadybugDBTemplate;
import com.thecookiezen.ladybugdb.spring.transaction.LadybugDBTransactionManager;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Profile("ladybugdb")
@EnableTransactionManagement
@EnableLadybugDBRepositories(basePackages = "com.thecookiezen.archiledger.infrastructure.persistence.ladybugdb")
public class LadybugDBConfig {

    private static final Logger logger = LoggerFactory.getLogger(LadybugDBConfig.class);

    @Value("${ladybugdb.pool.max-total:10}")
    private int poolMaxTotal;

    @Value("${ladybugdb.pool.max-idle:5}")
    private int poolMaxIdle;

    @Value("${ladybugdb.pool.min-idle:2}")
    private int poolMinIdle;

    @Value("${ladybugdb.data-dir:}")
    private String dataDir;

    @Bean(destroyMethod = "close")
    public Database database() {
        if (dataDir == null || dataDir.isBlank()) {
            logger.info("No data directory configured, creating in-memory LadybugDB database");
            return new Database(":memory:");
        }

        Path dataDirPath = Path.of(dataDir);
        boolean isNewDatabase = !Files.exists(dataDirPath.resolve("data"));

        if (isNewDatabase) {
            logger.info("Creating new persistent LadybugDB database at: {}", dataDirPath.toAbsolutePath());
        } else {
            logger.info("Loaded existing LadybugDB database from: {}", dataDirPath.toAbsolutePath());
        }

        return new Database(dataDir);
    }

    @Bean
    public LadybugDBConnectionFactory connectionFactory(Database database) {
        return new PooledConnectionFactory(database);
    }

    @Bean
    public LadybugDBTemplate ladybugDBTemplate(LadybugDBConnectionFactory connectionFactory) {
        return new LadybugDBTemplate(connectionFactory);
    }

    @Bean
    public PlatformTransactionManager transactionManager(LadybugDBConnectionFactory connectionFactory) {
        return new LadybugDBTransactionManager(connectionFactory);
    }
}
