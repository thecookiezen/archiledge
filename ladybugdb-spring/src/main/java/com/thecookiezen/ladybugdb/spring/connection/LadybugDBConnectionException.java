package com.thecookiezen.ladybugdb.spring.connection;

/**
 * Exception thrown when a connection cannot be obtained or released.
 */
public class LadybugDBConnectionException extends RuntimeException {

    public LadybugDBConnectionException(String message) {
        super(message);
    }

    public LadybugDBConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
