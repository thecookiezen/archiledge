package com.thecookiezen.ladybugdb.spring.core;

import com.ladybugdb.Connection;

/**
 * Callback interface for LadybugDB operations.
 * Used with {@link LadybugDBTemplate#execute(LadybugDBCallback)}.
 *
 * @param <T> the result type
 */
@FunctionalInterface
public interface LadybugDBCallback<T> {

    /**
     * Execute operations using the provided connection.
     *
     * @param connection the LadybugDB connection
     * @return the result of the operation
     */
    T doInLadybugDB(Connection connection);
}
