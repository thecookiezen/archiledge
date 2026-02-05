package com.thecookiezen.ladybugdb.spring.connection;

import com.ladybugdb.Connection;
import com.ladybugdb.Database;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * A connection factory that pools LadybugDB connections using Apache Commons
 * Pool2.
 */
public class PooledConnectionFactory implements LadybugDBConnectionFactory {

    private static final Logger logger = LoggerFactory.getLogger(PooledConnectionFactory.class);

    private final Database database;
    private final GenericObjectPool<Connection> pool;

    /**
     * Creates a pooled connection factory with default pool configuration.
     *
     * @param database the LadybugDB database instance
     */
    public PooledConnectionFactory(Database database) {
        this(database, createDefaultPoolConfig());
    }

    /**
     * Creates a pooled connection factory with custom pool configuration.
     *
     * @param database   the LadybugDB database instance
     * @param poolConfig the pool configuration
     */
    public PooledConnectionFactory(Database database, GenericObjectPoolConfig<Connection> poolConfig) {
        this.database = database;
        this.pool = new GenericObjectPool<>(new ConnectionPooledObjectFactory(database), poolConfig);
        logger.info("Created connection pool with maxTotal={}, maxIdle={}, minIdle={}",
                poolConfig.getMaxTotal(), poolConfig.getMaxIdle(), poolConfig.getMinIdle());
    }

    private static GenericObjectPoolConfig<Connection> createDefaultPoolConfig() {
        GenericObjectPoolConfig<Connection> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(10);
        config.setMaxIdle(5);
        config.setMinIdle(2);
        config.setMaxWait(Duration.ofSeconds(30));
        config.setTestOnBorrow(true);
        config.setTestOnReturn(false);
        config.setTestWhileIdle(true);
        config.setTimeBetweenEvictionRuns(Duration.ofMinutes(1));
        config.setMinEvictableIdleDuration(Duration.ofMinutes(5));
        return config;
    }

    @Override
    public Connection getConnection() {
        try {
            Connection connection = pool.borrowObject();
            logger.debug("Borrowed connection from pool. Active: {}, Idle: {}",
                    pool.getNumActive(), pool.getNumIdle());
            return connection;
        } catch (Exception e) {
            throw new LadybugDBConnectionException("Failed to borrow connection from pool", e);
        }
    }

    @Override
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            pool.returnObject(connection);
            logger.debug("Returned connection to pool. Active: {}, Idle: {}",
                    pool.getNumActive(), pool.getNumIdle());
        }
    }

    @Override
    public Database getDatabase() {
        return database;
    }

    @Override
    public void close() {
        logger.info("Closing connection pool");
        pool.close();
    }

    public int getNumActive() {
        return pool.getNumActive();
    }

    public int getNumIdle() {
        return pool.getNumIdle();
    }

    /**
     * Internal factory for creating pooled connection objects.
     */
    private static class ConnectionPooledObjectFactory extends BasePooledObjectFactory<Connection> {

        private final Database database;

        ConnectionPooledObjectFactory(Database database) {
            this.database = database;
        }

        @Override
        public Connection create() {
            logger.debug("Creating new connection for pool");
            return new Connection(database);
        }

        @Override
        public PooledObject<Connection> wrap(Connection connection) {
            return new DefaultPooledObject<>(connection);
        }

        @Override
        public void destroyObject(PooledObject<Connection> p) throws Exception {
            logger.debug("Destroying pooled connection");
            Connection connection = p.getObject();
            if (connection != null) {
                connection.close();
            }
        }

        @Override
        public boolean validateObject(PooledObject<Connection> p) {
            // LadybugDB connections don't have a ping method, assume valid if not null
            return p.getObject() != null;
        }
    }
}
