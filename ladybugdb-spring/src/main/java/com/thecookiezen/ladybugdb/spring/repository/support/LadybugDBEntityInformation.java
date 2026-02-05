package com.thecookiezen.ladybugdb.spring.repository.support;

import org.springframework.data.repository.core.EntityInformation;

/**
 * Entity information for LadybugDB entities.
 *
 * @param <T>  the entity type
 * @param <ID> the ID type
 */
public class LadybugDBEntityInformation<T, ID> implements EntityInformation<T, ID> {

    private final EntityMetadata<T> metadata;

    public LadybugDBEntityInformation(Class<T> domainClass) {
        this.metadata = new EntityMetadata<>(domainClass);
    }

    @Override
    public boolean isNew(T entity) {
        ID id = getId(entity);
        return id == null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ID getId(T entity) {
        return metadata.getId(entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<ID> getIdType() {
        if (metadata.getIdField() != null) {
            return (Class<ID>) metadata.getIdField().getType();
        }
        return (Class<ID>) Object.class;
    }

    @Override
    public Class<T> getJavaType() {
        return metadata.getEntityType();
    }
}
