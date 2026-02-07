package com.thecookiezen.ladybugdb.spring.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base repository interface for LadybugDB node entities.
 * Nodes have user-defined primary keys and are stored in node tables.
 *
 * @param <T>  the node entity type
 * @param <ID> the primary key type
 * @param <R>  the relationship entity type
 * @param <S>  the source node entity type
 */
@NoRepositoryBean
public interface NodeRepository<T, ID, R, S> extends CrudRepository<T, ID> {

    /**
     * Creates a relationship between source and target nodes.
     *
     * @param source       the source node
     * @param target       the target node
     * @param relationship the relationship entity with properties
     * @return the created relationship with its internal ID
     */
    R createRelation(S source, T target, R relationship);

    /**
     * Finds all relationships originating from the given source node.
     */
    List<R> findRelationsBySource(S source);

    /**
     * Finds all relationships of this type.
     */
    List<R> findAllRelations();

    /**
     * Deletes the given relationship.
     */
    void deleteRelation(R relationship);

    /**
     * Deletes all relationships originating from the given source node.
     */
    void deleteRelationBySource(T source);

    /**
     * Finds a relationship by its internal ID.
     */
    Optional<R> findRelationById(ID id);
}
