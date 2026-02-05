package com.thecookiezen.ladybugdb.spring.repository.support;

import com.thecookiezen.ladybugdb.spring.annotation.Destination;
import com.thecookiezen.ladybugdb.spring.annotation.RelationshipEntity;
import com.thecookiezen.ladybugdb.spring.annotation.Source;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Metadata about a relationship entity type, including type name
 * and source/target references.
 *
 * @param <R> the relationship entity type
 */
public class RelationshipMetadata<R> {

    private static final Pattern CAMEL_TO_UPPER_SNAKE = Pattern.compile("([a-z])([A-Z])");

    private final Class<R> relationshipType;
    private final String relationshipTypeName;
    private final Field sourceField;
    private final Field targetField;
    private final Class<?> sourceType;
    private final Class<?> targetType;

    public RelationshipMetadata(Class<R> relationshipType) {
        this.relationshipType = relationshipType;
        this.relationshipTypeName = determineTypeName(relationshipType);
        this.sourceField = findSourceField(relationshipType);
        this.targetField = findTargetField(relationshipType);
        this.sourceType = sourceField != null ? sourceField.getType() : Object.class;
        this.targetType = targetField != null ? targetField.getType() : Object.class;
    }

    private String determineTypeName(Class<R> relationshipType) {
        RelationshipEntity annotation = relationshipType.getAnnotation(RelationshipEntity.class);
        if (annotation != null && !annotation.type().isEmpty()) {
            return annotation.type();
        }

        String className = relationshipType.getSimpleName();
        if (className.endsWith("Relationship")) {
            className = className.substring(0, className.length() - 12);
        } else if (className.endsWith("Rel")) {
            className = className.substring(0, className.length() - 3);
        }

        Matcher matcher = CAMEL_TO_UPPER_SNAKE.matcher(className);
        return matcher.replaceAll("$1_$2").toUpperCase();
    }

    private Field findSourceField(Class<R> relationshipType) {
        for (Field field : relationshipType.getDeclaredFields()) {
            if (field.isAnnotationPresent(Source.class)) {
                field.setAccessible(true);
                return field;
            }
        }
        // Fallback: look for field named "source" or "from"
        try {
            Field sourceField = relationshipType.getDeclaredField("source");
            sourceField.setAccessible(true);
            return sourceField;
        } catch (NoSuchFieldException e) {
            try {
                Field fromField = relationshipType.getDeclaredField("from");
                fromField.setAccessible(true);
                return fromField;
            } catch (NoSuchFieldException ex) {
                return null;
            }
        }
    }

    private Field findTargetField(Class<R> relationshipType) {
        for (Field field : relationshipType.getDeclaredFields()) {
            if (field.isAnnotationPresent(Destination.class)) {
                field.setAccessible(true);
                return field;
            }
        }
        // Fallback: look for field named "target" or "to"
        try {
            Field targetField = relationshipType.getDeclaredField("target");
            targetField.setAccessible(true);
            return targetField;
        } catch (NoSuchFieldException e) {
            try {
                Field toField = relationshipType.getDeclaredField("to");
                toField.setAccessible(true);
                return toField;
            } catch (NoSuchFieldException ex) {
                return null;
            }
        }
    }

    public Class<R> getRelationshipType() {
        return relationshipType;
    }

    public String getRelationshipTypeName() {
        return relationshipTypeName;
    }

    public Field getSourceField() {
        return sourceField;
    }

    public Field getTargetField() {
        return targetField;
    }

    public Class<?> getSourceType() {
        return sourceType;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    @SuppressWarnings("unchecked")
    public <S> S getSource(R relationship) {
        if (sourceField == null) {
            return null;
        }
        try {
            return (S) sourceField.get(relationship);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access source field", e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getTarget(R relationship) {
        if (targetField == null) {
            return null;
        }
        try {
            return (T) targetField.get(relationship);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access target field", e);
        }
    }

    public void setSource(R relationship, Object source) {
        if (sourceField == null) {
            throw new IllegalStateException(
                    "No source field found for relationship type: " + relationshipType.getName());
        }
        try {
            sourceField.set(relationship, source);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set source field", e);
        }
    }

    public void setTarget(R relationship, Object target) {
        if (targetField == null) {
            throw new IllegalStateException(
                    "No target field found for relationship type: " + relationshipType.getName());
        }
        try {
            targetField.set(relationship, target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set target field", e);
        }
    }
}
