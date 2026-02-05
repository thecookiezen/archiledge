package com.thecookiezen.ladybugdb.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a LadybugDB node entity.
 * Node entities are stored in node tables and have user-defined primary keys.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NodeEntity {

    /**
     * The label for this node type. Defaults to the simple class name
     * (with "Entity" suffix removed if present).
     */
    String label() default "";
}
