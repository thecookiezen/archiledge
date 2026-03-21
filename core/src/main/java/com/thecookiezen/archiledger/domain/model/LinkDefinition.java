package com.thecookiezen.archiledger.domain.model;

public record LinkDefinition(MemoryNoteId source, MemoryNoteId target, String relationType, String context) {
    public LinkDefinition {
        if (source == null) {
            throw new IllegalArgumentException("LinkDefinition source cannot be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("LinkDefinition target cannot be null");
        }
        if (relationType == null || relationType.isBlank()) {
            throw new IllegalArgumentException("LinkDefinition relationType cannot be null or blank");
        }
        if (context == null || context.isBlank()) {
            throw new IllegalArgumentException("LinkDefinition context cannot be null or blank");
        }
    }

    public LinkDefinition(String source, String target, String relationType, String context) {
        this(new MemoryNoteId(source), new MemoryNoteId(target), relationType, context);
    }

    public NoteLink toNoteLink() {
        return new NoteLink(target, relationType, context);
    }
}
