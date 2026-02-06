package com.thecookiezen.ladybugdb.spring.repository.support;

import com.thecookiezen.ladybugdb.spring.core.LadybugDBTemplate;
import com.thecookiezen.ladybugdb.spring.mapper.ValueMappers;
import com.thecookiezen.ladybugdb.spring.repository.NodeRepository;

import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of {@link NodeRepository}.
 * Provides basic CRUD operations for node entities using
 * {@link LadybugDBTemplate}.
 *
 * @param <T>  the node entity type
 * @param <ID> the primary key type
 */
public class SimpleNodeRepository<T, ID> implements NodeRepository<T, ID> {

    private static final Logger logger = LoggerFactory.getLogger(SimpleNodeRepository.class);

    @SuppressWarnings("unused")
    private final Class<T> domainType;
    protected final LadybugDBTemplate template;
    protected final NodeMetadata<T> metadata;
    protected final EntityDescriptor<T> descriptor;

    public SimpleNodeRepository(LadybugDBTemplate template, Class<T> domainType, EntityDescriptor<T> descriptor) {
        this.template = template;
        this.domainType = domainType;
        this.metadata = new NodeMetadata<>(domainType);
        this.descriptor = descriptor;
        logger.debug("Created node repository for entity type: {}", domainType.getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S extends T> S save(S entity) {
        logger.debug("Saving node entity: {}", entity);

        Node n = Cypher.node(metadata.getNodeLabel()).named("n")
                .withProperties(metadata.getIdPropertyName(), Cypher.literalOf(metadata.getId(entity)));

        var setOperations = descriptor.writer().decompose(entity).entrySet().stream()
                .map(e -> n.property(e.getKey()).to(Cypher.literalOf(e.getValue())))
                .toList();

        Statement statement = Cypher
                .merge(n)
                .set(setOperations)
                .returning(n)
                .build();

        return (S) template.queryForObject(statement, descriptor.reader())
                .orElseThrow(() -> new RuntimeException("Failed to save node entity: " + entity));
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        for (S entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    @Override
    public Optional<T> findById(ID id) {
        logger.debug("Finding node by ID: {}", id);

        Node n = Cypher.node(metadata.getNodeLabel()).named("n")
                .withProperties(metadata.getIdPropertyName(), Cypher.literalOf(id));

        Statement statement = Cypher.match(n)
                .returning(Cypher.asterisk())
                .build();

        return template.queryForObject(statement, descriptor.reader());
    }

    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    @Override
    public Iterable<T> findAll() {
        logger.debug("Finding all nodes of type: {}", metadata.getNodeLabel());
        Statement statement = Cypher.match(Cypher.node(metadata.getNodeLabel()).named("n"))
                .returning(Cypher.asterisk())
                .build();

        return template.query(statement, descriptor.reader());
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        List<T> result = new ArrayList<>();
        for (ID id : ids) {
            findById(id).ifPresent(result::add);
        }
        return result;
    }

    @Override
    public long count() {
        logger.debug("Counting nodes of type: {}", metadata.getNodeLabel());
        Node n = Cypher.node(metadata.getNodeLabel()).named("n");
        Statement statement = Cypher.match(n)
                .returning(Cypher.count(n).as("count"))
                .build();

        return template.queryForObject(statement, (row, meta) -> (Long) ValueMappers.asLong(row.getValue(0)))
                .orElseThrow(() -> new RuntimeException("Failed to count nodes of type: " + metadata.getNodeLabel()));
    }

    @Override
    public void deleteById(ID id) {
        logger.debug("Deleting node by ID: {}", id);

        Node n = Cypher.node(metadata.getNodeLabel()).named("n")
                .withProperties(metadata.getIdPropertyName(), Cypher.literalOf(id));

        Statement statement = Cypher.match(n)
                .delete(n)
                .build();

        template.execute(statement);
    }

    @Override
    public void delete(T entity) {
        ID id = metadata.getId(entity);
        deleteById(id);
    }

    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {
        for (ID id : ids) {
            deleteById(id);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        for (T entity : entities) {
            delete(entity);
        }
    }

    @Override
    public void deleteAll() {
        logger.debug("Deleting all nodes of type: {}", metadata.getNodeLabel());
        throw new UnsupportedOperationException(
                "Generic deleteAll not implemented. Override this method or use a concrete repository.");
    }

    protected LadybugDBTemplate getTemplate() {
        return template;
    }

    protected NodeMetadata<T> getMetadata() {
        return metadata;
    }
}
