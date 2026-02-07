package com.thecookiezen.ladybugdb.spring.core;

import com.ladybugdb.Connection;

/**
 * Holder for a LadybugDB connection used for transaction synchronization.
 * Wraps a connection and tracks whether it should be released after use.
 */
public class ConnectionHolder {

    private final Connection connection;
    private boolean transactionActive;
    private boolean synchronizedWithTransaction;

    public ConnectionHolder(Connection connection) {
        this.connection = connection;
        this.transactionActive = false;
        this.synchronizedWithTransaction = false;
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isTransactionActive() {
        return transactionActive;
    }

    public void setTransactionActive(boolean transactionActive) {
        this.transactionActive = transactionActive;
    }

    public boolean isSynchronizedWithTransaction() {
        return synchronizedWithTransaction;
    }

    public void setSynchronizedWithTransaction(boolean synchronizedWithTransaction) {
        this.synchronizedWithTransaction = synchronizedWithTransaction;
    }

    public void released() {
        // Clean up if needed
    }
}
