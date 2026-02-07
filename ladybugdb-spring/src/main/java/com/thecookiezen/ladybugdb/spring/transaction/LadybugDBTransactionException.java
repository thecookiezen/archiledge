package com.thecookiezen.ladybugdb.spring.transaction;

import org.springframework.transaction.TransactionException;

/**
 * Exception thrown when a transaction operation fails.
 */
public class LadybugDBTransactionException extends TransactionException {

    public LadybugDBTransactionException(String message) {
        super(message);
    }

    public LadybugDBTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
