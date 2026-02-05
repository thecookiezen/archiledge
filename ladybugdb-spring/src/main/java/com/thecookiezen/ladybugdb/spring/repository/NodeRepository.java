package com.thecookiezen.ladybugdb.spring.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base repository interface for LadybugDB node entities.
 * Nodes have user-defined primary keys and are stored in node tables.
 *
 * @param <T>  the node entity type
 * @param <ID> the primary key type
 */
@NoRepositoryBean
public interface NodeRepository<T, ID> extends CrudRepository<T, ID> {

}
