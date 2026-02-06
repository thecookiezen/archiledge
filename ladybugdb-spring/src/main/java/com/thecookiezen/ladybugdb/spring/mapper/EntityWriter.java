package com.thecookiezen.ladybugdb.spring.mapper;

import java.util.Map;

public interface EntityWriter<T> {
    Map<String, Object> decompose(T entity);
}
