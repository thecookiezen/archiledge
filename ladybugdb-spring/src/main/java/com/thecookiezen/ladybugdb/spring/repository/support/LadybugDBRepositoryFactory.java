package com.thecookiezen.ladybugdb.spring.repository.support;

import com.thecookiezen.ladybugdb.spring.core.LadybugDBTemplate;
import com.thecookiezen.ladybugdb.spring.repository.NodeRepository;
import com.thecookiezen.ladybugdb.spring.repository.RelationshipRepository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * Factory for creating LadybugDB repository instances.
 * Detects whether the repository interface extends {@link NodeRepository}
 * or {@link RelationshipRepository} and creates the appropriate implementation.
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
        Class<?> repositoryInterface = metadata.getRepositoryInterface();

        if (RelationshipRepository.class.isAssignableFrom(repositoryInterface)) {
            // For relationships, we need source and target types from generics
            // This is a simplified implementation
            return new SimpleRelationshipRepository<>(
                    template,
                    metadata.getDomainType(),
                    Object.class,
                    Object.class);
        }

        return new SimpleNodeRepository<>(template, metadata.getDomainType());
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        Class<?> repositoryInterface = metadata.getRepositoryInterface();

        if (RelationshipRepository.class.isAssignableFrom(repositoryInterface)) {
            return SimpleRelationshipRepository.class;
        }

        return SimpleNodeRepository.class;
    }
}
