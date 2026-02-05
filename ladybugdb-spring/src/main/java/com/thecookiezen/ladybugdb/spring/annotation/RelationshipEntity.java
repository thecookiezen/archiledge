package com.thecookiezen.ladybugdb.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a LadybugDB relationship entity.
 * Relationship entities connect two node entities and have internally-generated
 * IDs.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RelationshipEntity {

    /**
     * The relationship type name. Defaults to the simple class name
     * in UPPER_SNAKE_CASE.
     */
    String type() default "";
}
