package com.thecookiezen.ladybugdb.spring.repository.support;

import com.thecookiezen.ladybugdb.spring.core.LadybugDBTemplate;
import com.thecookiezen.ladybugdb.spring.repository.RelationshipRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Default implementation of {@link RelationshipRepository}.
 * Provides operations for relationship entities using
 * {@link LadybugDBTemplate}.
 *
 * @param <R> the relationship entity type
 * @param <S> the source node entity type
 * @param <T> the target node entity type
 */
public class SimpleRelationshipRepository<R, S, T> implements RelationshipRepository<R, S, T> {

    private static final Logger logger = LoggerFactory.getLogger(SimpleRelationshipRepository.class);

    protected final LadybugDBTemplate template;
    protected final RelationshipMetadata<R> metadata;
    protected final NodeMetadata<S> sourceMetadata;
    protected final NodeMetadata<T> targetMetadata;

    public SimpleRelationshipRepository(
            LadybugDBTemplate template,
            Class<R> relationshipType,
            Class<S> sourceType,
            Class<T> targetType) {
        this.template = template;
        this.metadata = new RelationshipMetadata<>(relationshipType);
        this.sourceMetadata = new NodeMetadata<>(sourceType);
        this.targetMetadata = new NodeMetadata<>(targetType);
        logger.debug("Created relationship repository for type: {} ({}->{})",
                relationshipType.getName(), sourceType.getSimpleName(), targetType.getSimpleName());
    }

    @Override
    public R create(S source, T target, R relationship) {
        logger.debug("Creating relationship: {} -> {}", source, target);
        throw new UnsupportedOperationException(
                "Generic create not implemented. Override this method or use a concrete repository.");
    }

    @Override
    public Optional<R> findById(Long id) {
        logger.debug("Finding relationship by ID: {}", id);
        throw new UnsupportedOperationException(
                "Generic findById not implemented. Override this method or use a concrete repository.");
    }

    @Override
    public List<R> findBySource(S source) {
        logger.debug("Finding relationships by source: {}", source);
        throw new UnsupportedOperationException(
                "Generic findBySource not implemented. Override this method or use a concrete repository.");
    }

    @Override
    public List<R> findByTarget(T target) {
        logger.debug("Finding relationships by target: {}", target);
        throw new UnsupportedOperationException(
                "Generic findByTarget not implemented. Override this method or use a concrete repository.");
    }

    @Override
    public List<R> findBetween(S source, T target) {
        logger.debug("Finding relationships between: {} and {}", source, target);
        throw new UnsupportedOperationException(
                "Generic findBetween not implemented. Override this method or use a concrete repository.");
    }

    @Override
    public List<R> findAll() {
        logger.debug("Finding all relationships of type: {}", metadata.getRelationshipTypeName());
        throw new UnsupportedOperationException(
                "Generic findAll not implemented. Override this method or use a concrete repository.");
    }

    @Override
    public void delete(R relationship) {
        logger.debug("Deleting relationship: {}", relationship);
        throw new UnsupportedOperationException(
                "Generic delete not implemented. Override this method or use a concrete repository.");
    }

    @Override
    public void deleteById(Long id) {
        logger.debug("Deleting relationship by ID: {}", id);
        throw new UnsupportedOperationException(
                "Generic deleteById not implemented. Override this method or use a concrete repository.");
    }

    @Override
    public void deleteBetween(S source, T target) {
        logger.debug("Deleting relationships between: {} and {}", source, target);
        throw new UnsupportedOperationException(
                "Generic deleteBetween not implemented. Override this method or use a concrete repository.");
    }

    protected LadybugDBTemplate getTemplate() {
        return template;
    }

    protected RelationshipMetadata<R> getMetadata() {
        return metadata;
    }

    protected NodeMetadata<S> getSourceMetadata() {
        return sourceMetadata;
    }

    protected NodeMetadata<T> getTargetMetadata() {
        return targetMetadata;
    }
}
