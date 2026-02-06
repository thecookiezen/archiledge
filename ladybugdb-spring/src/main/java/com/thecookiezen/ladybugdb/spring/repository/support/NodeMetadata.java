package com.thecookiezen.ladybugdb.spring.repository.support;

import com.thecookiezen.ladybugdb.spring.annotation.NodeEntity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class NodeMetadata<T> {

    private final Class<T> entityType;
    private final String nodeLabel;
    private final Field idField;
    private final String idPropertyName;
    private final List<String> propertyNames;

    public NodeMetadata(Class<T> entityType) {
        this.entityType = entityType;
        this.nodeLabel = determineNodeLabel(entityType);
        this.idField = findIdField(entityType);
        this.idPropertyName = idField != null ? idField.getName() : "id";
        this.propertyNames = extractPropertyNames(entityType);
    }

    private List<String> extractPropertyNames(Class<T> entityType) {
        return Arrays.stream(entityType.getDeclaredFields())
                .map(Field::getName)
                .toList();
    }

    public List<String> getPropertyNames() {
        return propertyNames;
    }

    private String determineNodeLabel(Class<T> entityType) {
        NodeEntity annotation = entityType.getAnnotation(NodeEntity.class);
        if (annotation != null && !annotation.label().isEmpty()) {
            return annotation.label();
        }
        String className = entityType.getSimpleName();
        if (className.endsWith("Entity") && className.length() > 6) {
            return className.substring(0, className.length() - 6);
        }
        return className;
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
            return null;
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
