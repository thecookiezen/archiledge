package com.thecookiezen.archiledger.infrastructure.mcp.dto;

import com.thecookiezen.archiledger.domain.model.NoteLink;

public record NoteLinkDto(String target, String relationType, String context) {
    public NoteLinkDto {
        if (target == null || target.isBlank()) {
            throw new IllegalArgumentException("NoteLink target cannot be null or blank");
        }
        if (relationType == null || relationType.isBlank()) {
            throw new IllegalArgumentException("NoteLink relationType cannot be null or blank");
        }
        if (context == null || context.isBlank()) {
            throw new IllegalArgumentException("NoteLink context cannot be null or blank");
        }
    }

    public NoteLink toDomain() {
        return new NoteLink(target, relationType, context);
    }

    public static NoteLinkDto fromDomain(NoteLink link) {
        return new NoteLinkDto(link.target().value(), link.relationType(), link.context());
    }
}
