package com.thecookiezen.ladybugdb.spring.repository.support;

import com.thecookiezen.ladybugdb.spring.core.LadybugDBTemplate;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * Factory for creating LadybugDB repository instances.
 * Extends Spring Data's {@link RepositoryFactorySupport} to integrate with
 * Spring's repository infrastructure.
 */
public class LadybugDBRepositoryFactory extends RepositoryFactorySupport {

    private final LadybugDBTemplate template;

    public LadybugDBRepositoryFactory(LadybugDBTemplate template) {
        this.template = template;
    }

    @Override
    public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        return new LadybugDBEntityInformation<>(domainClass);
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation metadata) {
        return new SimpleLadybugDBRepository<>(template, metadata.getDomainType());
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return SimpleLadybugDBRepository.class;
    }
}
