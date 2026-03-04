package com.thecookiezen.archiledger.domain.model;

public record NoteLink(MemoryNoteId target, String relationType) {
    public NoteLink {
        if (target == null) {
            throw new IllegalArgumentException("NoteLink target cannot be null");
        }
        if (relationType == null || relationType.isBlank()) {
            throw new IllegalArgumentException("NoteLink relationType cannot be null or blank");
        }
    }

    public NoteLink(String target, String relationType) {
        this(new MemoryNoteId(target), relationType);
    }
}
