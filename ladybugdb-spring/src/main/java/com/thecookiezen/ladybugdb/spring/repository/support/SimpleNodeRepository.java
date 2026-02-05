package com.thecookiezen.ladybugdb.spring.repository.support;

import com.thecookiezen.ladybugdb.spring.core.LadybugDBTemplate;
import com.thecookiezen.ladybugdb.spring.repository.NodeRepository;
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

    protected final LadybugDBTemplate template;
    protected final NodeMetadata<T> metadata;

    public SimpleNodeRepository(LadybugDBTemplate template, Class<T> domainType) {
        this.template = template;
        this.metadata = new NodeMetadata<>(domainType);
        logger.debug("Created node repository for entity type: {}", domainType.getName());
    }

    @Override
    public <S extends T> S save(S entity) {
        logger.debug("Saving node entity: {}", entity);
        throw new UnsupportedOperationException(
                "Generic save not implemented. Override this method or use a concrete repository.");
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
        throw new UnsupportedOperationException(
                "Generic findById not implemented. Override this method or use a concrete repository.");
    }

    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    @Override
    public Iterable<T> findAll() {
        logger.debug("Finding all nodes of type: {}", metadata.getNodeLabel());
        throw new UnsupportedOperationException(
                "Generic findAll not implemented. Override this method or use a concrete repository.");
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
        throw new UnsupportedOperationException(
                "Generic count not implemented. Override this method or use a concrete repository.");
    }

    @Override
    public void deleteById(ID id) {
        logger.debug("Deleting node by ID: {}", id);
        throw new UnsupportedOperationException(
                "Generic deleteById not implemented. Override this method or use a concrete repository.");
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
