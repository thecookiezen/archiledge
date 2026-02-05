package com.thecookiezen.ladybugdb.spring.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base repository interface for LadybugDB repositories.
 * Extends Spring Data's {@link CrudRepository} to provide standard CRUD
 * operations.
 *
 * @param <T>  the domain type the repository manages
 * @param <ID> the type of the id of the entity the repository manages
 */
@NoRepositoryBean
public interface LadybugDBRepository<T, ID> extends CrudRepository<T, ID> {

}
