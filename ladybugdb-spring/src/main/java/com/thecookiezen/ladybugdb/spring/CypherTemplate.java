package com.thecookiezen.ladybugdb.spring;

import com.ladybugdb.Connection;
import com.ladybugdb.QueryResult;
import org.neo4j.cypherdsl.core.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Template class for executing Cypher queries on LadybugDB.
 * Provides convenient methods for executing queries and mapping results to
 * domain objects.
 */
public class CypherTemplate {

    private static final Logger logger = LoggerFactory.getLogger(CypherTemplate.class);

    private final Connection connection;

    public CypherTemplate(Connection connection) {
        this.connection = connection;
    }

    /**
     * Executes a Cypher statement (typically for write operations like CREATE,
     * MERGE, DELETE).
     *
     * @param statement the Cypher DSL statement to execute
     */
    public void execute(Statement statement) {
        execute(statement.getCypher());
    }

    /**
     * Executes a raw Cypher query string (typically for write operations).
     *
     * @param cypher the Cypher query string
     */
    public void execute(String cypher) {
        logger.debug("Executing Cypher: {}", cypher);
        QueryResult result = connection.query(cypher);
        logger.debug("Execute result: {}", result);
    }

    /**
     * Executes a Cypher DSL statement and maps results using the provided
     * RowMapper.
     *
     * @param statement the Cypher DSL statement
     * @param rowMapper the mapper to convert each row to a domain object
     * @param <T>       the type of object to return
     * @return a list of mapped objects
     */
    public <T> List<T> query(Statement statement, RowMapper<T> rowMapper) {
        return query(statement.getCypher(), rowMapper);
    }

    /**
     * Executes a raw Cypher query and maps results using the provided RowMapper.
     *
     * @param cypher    the Cypher query string
     * @param rowMapper the mapper to convert each row to a domain object
     * @param <T>       the type of object to return
     * @return a list of mapped objects
     */
    public <T> List<T> query(String cypher, RowMapper<T> rowMapper) {
        logger.debug("Querying with Cypher: {}", cypher);

        QueryResult result = connection.query(cypher);
        List<T> results = new ArrayList<>();
        int rowNum = 0;

        while (result.hasNext()) {
            var row = result.getNext();
            try {
                T mapped = rowMapper.mapRow(row, rowNum++);
                results.add(mapped);
            } catch (Exception e) {
                throw new CypherMappingException("Error mapping row " + (rowNum - 1), e);
            }
        }

        logger.debug("Query returned {} results", results.size());
        return results;
    }

    /**
     * Executes a Cypher DSL statement and returns a single result.
     *
     * @param statement the Cypher DSL statement
     * @param rowMapper the mapper to convert the row to a domain object
     * @param <T>       the type of object to return
     * @return an Optional containing the mapped object, or empty if no results
     */
    public <T> Optional<T> queryForObject(Statement statement, RowMapper<T> rowMapper) {
        return queryForObject(statement.getCypher(), rowMapper);
    }

    /**
     * Executes a raw Cypher query and returns a single result.
     *
     * @param cypher    the Cypher query string
     * @param rowMapper the mapper to convert the row to a domain object
     * @param <T>       the type of object to return
     * @return an Optional containing the mapped object, or empty if no results
     */
    public <T> Optional<T> queryForObject(String cypher, RowMapper<T> rowMapper) {
        List<T> results = query(cypher, rowMapper);
        if (results.isEmpty()) {
            return Optional.empty();
        }
        if (results.size() > 1) {
            logger.warn("queryForObject returned {} results, expected 1", results.size());
        }
        return Optional.of(results.get(0));
    }

    /**
     * Executes a Cypher query and maps to a list of strings (for single-column
     * string results).
     *
     * @param statement   the Cypher DSL statement
     * @param columnIndex the index of the column to extract (0-indexed)
     * @return a list of strings
     */
    public List<String> queryForStringList(Statement statement, int columnIndex) {
        return queryForStringList(statement.getCypher(), columnIndex);
    }

    /**
     * Executes a Cypher query and maps to a list of strings (for single-column
     * string results).
     *
     * @param cypher      the Cypher query string
     * @param columnIndex the index of the column to extract (0-indexed)
     * @return a list of strings
     */
    public List<String> queryForStringList(String cypher, int columnIndex) {
        return query(cypher, (row, rowNum) -> row.getValue(columnIndex).getValue().toString());
    }

    /**
     * Exception thrown when mapping a row fails.
     */
    public static class CypherMappingException extends RuntimeException {
        public CypherMappingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
