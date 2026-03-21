package com.thecookiezen.archiledger.agenticmemory.domain;

public record SuggestedLink(
    String targetId,
    String relationType,
    String context
) {
    public SuggestedLink {
        if (targetId == null || targetId.isBlank()) {
            throw new IllegalArgumentException("SuggestedLink targetId cannot be null or blank");
        }
        if (relationType == null || relationType.isBlank()) {
            throw new IllegalArgumentException("SuggestedLink relationType cannot be null or blank");
        }
        if (context == null || context.isBlank()) {
            throw new IllegalArgumentException("SuggestedLink context cannot be null or blank");
        }
    }
}
