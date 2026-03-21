package com.thecookiezen.archiledger.agenticmemory.domain;

import java.util.List;

public record NeighborUpdate(
    String noteId,
    String newContext,
    List<String> newTags
) {
    public NeighborUpdate {
        newTags = newTags != null ? List.copyOf(newTags) : List.of();
        newContext = newContext != null ? newContext : "";
    }
}
