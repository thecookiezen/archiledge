package com.thecookiezen.ladybugdb.spring.transaction;

import com.ladybugdb.Connection;
import com.thecookiezen.ladybugdb.spring.core.ConnectionHolder;

/**
 * Transaction object holding the connection state for a LadybugDB transaction.
 */
public class LadybugDBTransactionObject {

    private ConnectionHolder connectionHolder;
    private boolean newConnectionHolder;
    private boolean rollbackOnly;

    public void setConnectionHolder(ConnectionHolder connectionHolder) {
        this.connectionHolder = connectionHolder;
    }

    public ConnectionHolder getConnectionHolder() {
        return connectionHolder;
    }

    public boolean hasConnectionHolder() {
        return connectionHolder != null;
    }

    public Connection getConnection() {
        return connectionHolder != null ? connectionHolder.getConnection() : null;
    }

    public boolean isNewConnectionHolder() {
        return newConnectionHolder;
    }

    public void setNewConnectionHolder(boolean newConnectionHolder) {
        this.newConnectionHolder = newConnectionHolder;
    }

    public boolean isRollbackOnly() {
        return rollbackOnly;
    }

    public void setRollbackOnly(boolean rollbackOnly) {
        this.rollbackOnly = rollbackOnly;
    }

    public void clear() {
        this.connectionHolder = null;
        this.newConnectionHolder = false;
        this.rollbackOnly = false;
    }
}
