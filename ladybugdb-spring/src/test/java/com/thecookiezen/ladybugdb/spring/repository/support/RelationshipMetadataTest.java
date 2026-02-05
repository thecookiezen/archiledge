package com.thecookiezen.ladybugdb.spring.repository.support;

import com.thecookiezen.ladybugdb.spring.annotation.Destination;
import com.thecookiezen.ladybugdb.spring.annotation.RelationshipEntity;
import com.thecookiezen.ladybugdb.spring.annotation.Source;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RelationshipMetadataTest {

    @Nested
    class TypeNameDerivation {

        @Test
        void shouldConvertCamelCaseToUpperSnakeCase() {
            RelationshipMetadata<FollowedBy> metadata = new RelationshipMetadata<>(FollowedBy.class);

            assertEquals("FOLLOWED_BY", metadata.getRelationshipTypeName());
        }

        @Test
        void shouldRemoveRelationshipSuffix() {
            RelationshipMetadata<LikesRelationship> metadata = new RelationshipMetadata<>(LikesRelationship.class);

            assertEquals("LIKES", metadata.getRelationshipTypeName());
        }

        @Test
        void shouldUseAnnotationType() {
            RelationshipMetadata<AnnotatedRelation> metadata = new RelationshipMetadata<>(AnnotatedRelation.class);

            assertEquals("CUSTOM_TYPE", metadata.getRelationshipTypeName());
        }
    }

    @Nested
    class SourceTargetDetection {

        @Test
        void shouldFindAnnotatedSourceField() {
            RelationshipMetadata<RelWithAnnotations> metadata = new RelationshipMetadata<>(RelWithAnnotations.class);

            assertNotNull(metadata.getSourceField());
            assertEquals("from", metadata.getSourceField().getName());
        }

        @Test
        void shouldFindAnnotatedTargetField() {
            RelationshipMetadata<RelWithAnnotations> metadata = new RelationshipMetadata<>(RelWithAnnotations.class);

            assertNotNull(metadata.getTargetField());
            assertEquals("to", metadata.getTargetField().getName());
        }

        @Test
        void shouldFindFallbackSourceField() {
            RelationshipMetadata<RelWithFallbackNames> metadata = new RelationshipMetadata<>(
                    RelWithFallbackNames.class);

            assertNotNull(metadata.getSourceField());
            assertEquals("source", metadata.getSourceField().getName());
        }

        @Test
        void shouldFindFallbackTargetField() {
            RelationshipMetadata<RelWithFallbackNames> metadata = new RelationshipMetadata<>(
                    RelWithFallbackNames.class);

            assertNotNull(metadata.getTargetField());
            assertEquals("target", metadata.getTargetField().getName());
        }
    }

    @Nested
    class SourceTargetAccess {

        @Test
        void shouldGetSourceFromRelationship() {
            RelationshipMetadata<RelWithAnnotations> metadata = new RelationshipMetadata<>(RelWithAnnotations.class);
            RelWithAnnotations rel = new RelWithAnnotations();
            rel.from = "sourceNode";

            Object source = metadata.getSource(rel);

            assertEquals("sourceNode", source);
        }

        @Test
        void shouldGetTargetFromRelationship() {
            RelationshipMetadata<RelWithAnnotations> metadata = new RelationshipMetadata<>(RelWithAnnotations.class);
            RelWithAnnotations rel = new RelWithAnnotations();
            rel.to = "targetNode";

            Object target = metadata.getTarget(rel);

            assertEquals("targetNode", target);
        }
    }

    static class FollowedBy {
        Object source;
        Object target;
    }

    static class LikesRelationship {
        Object source;
        Object target;
    }

    @RelationshipEntity(type = "CUSTOM_TYPE")
    static class AnnotatedRelation {
        Object source;
        Object target;
    }

    static class RelWithAnnotations {
        @Source
        String from;
        @Destination
        String to;
    }

    static class RelWithFallbackNames {
        String source;
        String target;
    }
}
