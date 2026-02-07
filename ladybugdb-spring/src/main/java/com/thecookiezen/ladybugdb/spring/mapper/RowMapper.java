package com.thecookiezen.ladybugdb.spring.mapper;

import java.util.Map;

import com.ladybugdb.Value;

/**
 * Functional interface for mapping a row from a LadybugDB query result to a
 * domain object.
 *
 * @param <T> the type of object to map to
 */
@FunctionalInterface
public interface RowMapper<T> {

    /**
     * Maps a single row of the query result to a domain object.
     *
     * @param struct the struct representing the current row
     * @return the mapped object
     * @throws Exception if an error occurs during mapping
     */
    T mapRow(Map<String, Value> struct) throws Exception;
}
