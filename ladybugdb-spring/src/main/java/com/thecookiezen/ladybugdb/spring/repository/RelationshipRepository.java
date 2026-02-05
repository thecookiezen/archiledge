package com.thecookiezen.ladybugdb.spring.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Base repository interface for LadybugDB relationship entities.
 * Relationships connect two node entities and have internally-generated IDs.
 *
 * @param <R> the relationship entity type
 * @param <S> the source node entity type
 * @param <T> the target node entity type
 */
@NoRepositoryBean
public interface RelationshipRepository<R, S, T> extends Repository<R, Long> {

    /**
     * Creates a relationship between source and target nodes.
     *
     * @param source       the source node
     * @param target       the target node
     * @param relationship the relationship entity with properties
     * @return the created relationship with its internal ID
     */
    R create(S source, T target, R relationship);

    /**
     * Finds a relationship by its internal edge ID.
     */
    Optional<R> findById(Long id);

    /**
     * Finds all relationships originating from the given source node.
     */
    List<R> findBySource(S source);

    /**
     * Finds all relationships pointing to the given target node.
     */
    List<R> findByTarget(T target);

    /**
     * Finds all relationships between the given source and target nodes.
     */
    List<R> findBetween(S source, T target);

    /**
     * Finds all relationships of this type.
     */
    List<R> findAll();

    /**
     * Deletes the given relationship.
     */
    void delete(R relationship);

    /**
     * Deletes a relationship by its internal edge ID.
     */
    void deleteById(Long id);

    /**
     * Deletes all relationships between source and target.
     */
    void deleteBetween(S source, T target);
}
