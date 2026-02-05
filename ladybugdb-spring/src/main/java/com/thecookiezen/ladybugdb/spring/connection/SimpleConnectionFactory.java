package com.thecookiezen.ladybugdb.spring.connection;

import com.ladybugdb.Connection;
import com.ladybugdb.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple connection factory that creates a new connection for each request.
 * Useful for testing or single-threaded applications.
 */
public class SimpleConnectionFactory implements LadybugDBConnectionFactory {

    private static final Logger logger = LoggerFactory.getLogger(SimpleConnectionFactory.class);

    private final Database database;

    public SimpleConnectionFactory(Database database) {
        this.database = database;
    }

    @Override
    public Connection getConnection() {
        logger.debug("Creating new connection");
        return new Connection(database);
    }

    @Override
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            logger.debug("Closing connection");
            connection.close();
        }
    }

    @Override
    public Database getDatabase() {
        return database;
    }

    @Override
    public void close() {
        // Nothing to close for simple factory
    }
}
