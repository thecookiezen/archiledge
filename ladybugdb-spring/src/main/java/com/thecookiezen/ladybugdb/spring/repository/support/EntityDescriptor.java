package com.thecookiezen.ladybugdb.spring.repository.support;

import com.thecookiezen.ladybugdb.spring.mapper.RowMapper;
import com.thecookiezen.ladybugdb.spring.mapper.EntityWriter;

public record EntityDescriptor<T>(Class<T> entityType, RowMapper<T> reader, EntityWriter<T> writer) {

}
