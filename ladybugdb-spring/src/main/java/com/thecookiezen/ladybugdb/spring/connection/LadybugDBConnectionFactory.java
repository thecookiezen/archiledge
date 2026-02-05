package com.thecookiezen.ladybugdb.spring.connection;

import com.ladybugdb.Connection;
import com.ladybugdb.Database;

/**
 * Factory interface for creating and managing LadybugDB connections.
 */
public interface LadybugDBConnectionFactory {

    /**
     * Obtains a connection from this factory.
     * The connection may be new or retrieved from a pool.
     *
     * @return a LadybugDB Connection
     */
    Connection getConnection();

    /**
     * Releases a connection back to the factory.
     * For pooled implementations, this returns the connection to the pool.
     *
     * @param connection the connection to release
     */
    void releaseConnection(Connection connection);

    /**
     * Returns the underlying database instance.
     *
     * @return the Database
     */
    Database getDatabase();

    /**
     * Closes this factory and releases all resources.
     */
    void close();
}
