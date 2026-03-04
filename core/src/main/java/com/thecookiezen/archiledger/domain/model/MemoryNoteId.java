package com.thecookiezen.archiledger.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record MemoryNoteId(String value) {
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public MemoryNoteId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("MemoryNoteId cannot be null or blank");
        }
    }

    @JsonValue
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
