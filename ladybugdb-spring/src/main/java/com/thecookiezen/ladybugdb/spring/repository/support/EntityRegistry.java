package com.thecookiezen.ladybugdb.spring.repository.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.thecookiezen.ladybugdb.spring.mapper.EntityWriter;
import com.thecookiezen.ladybugdb.spring.mapper.RowMapper;

public class EntityRegistry {
    private final Map<Class<?>, EntityDescriptor<?>> descriptors = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> EntityDescriptor<T> getDescriptor(Class<T> domainType) {
        return (EntityDescriptor<T>) descriptors.get(domainType);
    }

    public <T> void registerDescriptor(Class<T> domainType, RowMapper<T> reader, EntityWriter<T> writer) {
        descriptors.put(domainType, new EntityDescriptor<>(domainType, reader, writer));
    }
}
