package com.thecookiezen.ladybugdb.spring.repository.support;

import com.thecookiezen.ladybugdb.spring.core.LadybugDBTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * Factory bean for creating LadybugDB repository instances.
 * This bean is created for each repository interface and is responsible
 * for creating the actual repository implementation.
 *
 * @param <T>  the repository type
 * @param <S>  the domain type
 * @param <ID> the ID type
 */
public class LadybugDBRepositoryFactoryBean<T extends Repository<S, ID>, S, ID>
        extends RepositoryFactoryBeanSupport<T, S, ID> {

    private LadybugDBTemplate template;
    private EntityRegistry entityRegistry;

    protected LadybugDBRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Autowired
    public void setTemplate(LadybugDBTemplate template) {
        this.template = template;
    }

    @Autowired
    public void setEntityRegistry(EntityRegistry entityRegistry) {
        this.entityRegistry = entityRegistry;
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory() {
        return new LadybugDBRepositoryFactory(template, entityRegistry);
    }
}
