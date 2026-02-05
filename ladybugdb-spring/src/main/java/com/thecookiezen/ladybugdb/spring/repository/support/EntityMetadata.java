package com.thecookiezen.ladybugdb.spring.repository.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Metadata about an entity type, including its ID property.
 *
 * @param <T> the entity type
 */
public class EntityMetadata<T> {

    private final Class<T> entityType;
    private final String nodeLabel;
    private final Field idField;
    private final String idPropertyName;

    public EntityMetadata(Class<T> entityType) {
        this.entityType = entityType;
        this.nodeLabel = determineNodeLabel(entityType);
        this.idField = findIdField(entityType);
        this.idPropertyName = idField != null ? idField.getName() : "id";
    }

    private String determineNodeLabel(Class<T> entityType) {
        // Check for @Node annotation or similar
        // For now, use simple class name
        return entityType.getSimpleName();
    }

    private Field findIdField(Class<T> entityType) {
        for (Field field : entityType.getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                String annotationName = annotation.annotationType().getSimpleName();
                if ("Id".equals(annotationName) || "PrimaryKey".equals(annotationName)) {
                    field.setAccessible(true);
                    return field;
                }
            }
        }
        // Fallback: look for field named "id"
        try {
            Field idField = entityType.getDeclaredField("id");
            idField.setAccessible(true);
            return idField;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    public String getNodeLabel() {
        return nodeLabel;
    }

    public Field getIdField() {
        return idField;
    }

    public String getIdPropertyName() {
        return idPropertyName;
    }

    @SuppressWarnings("unchecked")
    public <ID> ID getId(T entity) {
        if (idField == null) {
            throw new IllegalStateException("No ID field found for entity type: " + entityType.getName());
        }
        try {
            return (ID) idField.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access ID field", e);
        }
    }

    public void setId(T entity, Object id) {
        if (idField == null) {
            throw new IllegalStateException("No ID field found for entity type: " + entityType.getName());
        }
        try {
            idField.set(entity, id);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set ID field", e);
        }
    }
}
