package com.thecookiezen.ladybugdb.spring.core;

import com.ladybugdb.Connection;
import com.ladybugdb.PreparedStatement;
import com.ladybugdb.QueryResult;
import com.ladybugdb.Value;
import com.thecookiezen.ladybugdb.spring.connection.LadybugDBConnectionFactory;
import com.thecookiezen.ladybugdb.spring.mapper.DefaultQueryRow;
import com.thecookiezen.ladybugdb.spring.mapper.QueryRow;
import com.thecookiezen.ladybugdb.spring.mapper.RowMapper;
import org.neo4j.cypherdsl.core.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Transaction-aware template for LadybugDB operations.
 * Central class for executing Cypher queries with proper
 * connection and transaction management.
 * <p>
 * When used within a Spring transaction, the same connection is reused
 * for all operations. Outside a transaction, a new connection is obtained
 * and released for each operation.
 */
public class LadybugDBTemplate {

    private static final Logger logger = LoggerFactory.getLogger(LadybugDBTemplate.class);

    private final LadybugDBConnectionFactory connectionFactory;

    public LadybugDBTemplate(LadybugDBConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**
     * Execute an operation using a callback function.
     * The connection is managed automatically based on transaction context.
     *
     * @param action the callback to execute
     * @param <T>    the result type
     * @return the result of the callback
     */
    public <T> T execute(LadybugDBCallback<T> action) {
        Connection connection = getConnection();
        boolean isNewConnection = !isConnectionBoundToTransaction();
        try {
            return action.doInLadybugDB(connection);
        } finally {
            if (isNewConnection) {
                releaseConnection(connection);
            }
        }
    }

    public void execute(String cypher) {
        execute(cypher, Map.of());
    }

    /**
     * Execute a Cypher statement (typically for write operations).
     *
     * @param statement the Cypher DSL statement
     */
    public void execute(Statement statement) {
        execute(statement.getCypher(), Map.of());
    }

    /**
     * Execute a raw Cypher query (typically for write operations).
     *
     * @param cypher     the Cypher query string
     * @param parameters the query parameters
     */
    public void execute(String cypher, Map<String, Object> parameters) {
        validateCypherSafety(cypher, parameters);
        execute(connection -> {
            logger.debug("Executing Cypher: {}", cypher);
            Map<String, Value> valueParameters = convertParameters(parameters);
            try (PreparedStatement statement = connection.prepare(cypher);
                    QueryResult result = connection.execute(statement, valueParameters)) {
                logger.debug("Execute result: {}", result);
            } finally {
                // Parameters should be closed if they are new objects
                valueParameters.values().forEach(v -> {
                    try {
                        v.close();
                    } catch (Exception e) {
                        /* ignore */ }
                });
            }
            return null;
        });
    }

    /**
     * Execute a Cypher statement and map results using the provided RowMapper.
     *
     * @param cypher     the Cypher query string
     * @param parameters the query parameters
     * @param rowMapper  the mapper to convert each row
     * @param <T>        the result type
     * @return stream of mapped results
     */
    public <T> Stream<T> stream(String cypher, Map<String, Object> parameters, RowMapper<T> rowMapper) {
        validateCypherSafety(cypher, parameters);

        Connection connection = getConnection();
        boolean isNewConnection = !isConnectionBoundToTransaction();

        Map<String, Value> valueParameters = convertParameters(parameters);
        PreparedStatement statement = connection.prepare(cypher);
        QueryResult result = connection.execute(statement, valueParameters);

        int numColumns = (int) result.getNumColumns();
        Map<String, Integer> columnToIndex = new HashMap<>(numColumns);
        for (int i = 0; i < numColumns; i++) {
            columnToIndex.put(result.getColumnName(i), i);
        }

        Value[] valuesArray = new Value[numColumns];
        QueryRow queryRow = new DefaultQueryRow(valuesArray, columnToIndex);

        Iterator<T> iterator = new Iterator<>() {
            @Override
            public boolean hasNext() {
                return result.hasNext();
            }

            @Override
            public T next() {
                var row = result.getNext();
                try {
                    for (int i = 0; i < numColumns; i++) {
                        valuesArray[i] = row.getValue(i);
                    }
                    return rowMapper.mapRow(queryRow);
                } catch (Exception e) {
                    throw new CypherMappingException("Error mapping row", e);
                } finally {
                    for (int i = 0; i < numColumns; i++) {
                        if (valuesArray[i] != null) {
                            valuesArray[i].close();
                            valuesArray[i] = null;
                        }
                    }
                }
            }
        };

        return StreamSupport
                .stream(Spliterators.spliterator(iterator, result.getNumTuples(), Spliterator.ORDERED), false)
                .onClose(() -> {
                    try {
                        result.close();
                        statement.close();
                        valueParameters.values().forEach(Value::close);
                        if (isNewConnection) {
                            releaseConnection(connection);
                        }
                    } catch (Exception e) {
                        logger.error("Error closing stream resources", e);
                    }
                });
    }

    public <T> List<T> query(String cypher, Map<String, Object> parameters, RowMapper<T> rowMapper) {
        return stream(cypher, parameters, rowMapper).toList();
    }

    public <T> List<T> query(Statement statement, RowMapper<T> rowMapper) {
        return query(statement.getCypher(), Map.of(), rowMapper);
    }

    public <T> List<T> query(String cypher, RowMapper<T> rowMapper) {
        return query(cypher, Map.of(), rowMapper);
    }

    public <T> Optional<T> queryForObject(Statement statement, RowMapper<T> rowMapper) {
        return queryForObject(statement.getCypher(), rowMapper);
    }

    /**
     * Execute a raw Cypher query and return a single result.
     *
     * @param cypher    the Cypher query string
     * @param rowMapper the mapper to convert the row
     * @param <T>       the result type
     * @return Optional containing the result, or empty if no results
     */
    public <T> Optional<T> queryForObject(String cypher, RowMapper<T> rowMapper) {
        return queryForObject(cypher, Map.of(), rowMapper);
    }

    public <T> Optional<T> queryForObject(String cypher, Map<String, Object> parameters, RowMapper<T> rowMapper) {
        return stream(cypher, parameters, rowMapper).findFirst();
    }

    /**
     * Execute a Cypher query and return a list of strings.
     *
     * @param statement  the Cypher DSL statement
     * @param columnName the column name to extract
     * @return list of string values
     */
    public List<String> queryForStringList(Statement statement, String columnName) {
        return queryForStringList(statement.getCypher(), columnName);
    }

    /**
     * Execute a raw Cypher query and return a list of strings.
     *
     * @param cypher     the Cypher query string
     * @param columnName the column name to extract
     * @return list of string values
     */
    public List<String> queryForStringList(String cypher, String columnName) {
        return query(cypher, (row) -> row.getValue(columnName).getValue().toString());
    }

    private void validateCypherSafety(String cypher, Map<String, Object> parameters) {
        if (cypher == null)
            return;

        if ((parameters == null || parameters.isEmpty()) && cypher.contains("'")) {
            long quoteCount = cypher.chars().filter(ch -> ch == '\'').count();
            if (quoteCount > 2) {
                logger.warn("SECURITY: Detected Cypher query with multiple literals and no parameters. " +
                        "Please use parameterized queries to prevent injection. Query: {}", cypher);
            }
        }
    }

    public LadybugDBConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    private Connection getConnection() {
        ConnectionHolder holder = (ConnectionHolder) TransactionSynchronizationManager.getResource(connectionFactory);
        if (holder != null) {
            return holder.getConnection();
        }

        logger.debug("Obtaining new connection from factory");
        return connectionFactory.getConnection();
    }

    private void releaseConnection(Connection connection) {
        connectionFactory.releaseConnection(connection);
    }

    private boolean isConnectionBoundToTransaction() {
        return TransactionSynchronizationManager.hasResource(connectionFactory);
    }

    private Map<String, Value> convertParameters(Map<String, Object> parameters) {
        if (parameters != null && !parameters.isEmpty()) {
            Map<String, Value> converted = new HashMap<>();
            parameters.forEach((key, value) -> converted.put(key, toValue(value)));
            return converted;
        }
        return Collections.emptyMap();
    }

    private Value toValue(Object obj) {
        if (obj == null) {
            return Value.createNull();
        }
        if (obj instanceof Value v) {
            return v;
        }
        return new Value(obj);
    }

    public static class CypherMappingException extends RuntimeException {
        public CypherMappingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}