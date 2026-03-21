package com.thecookiezen.archiledger.agenticmemory.domain;

import java.util.List;

public record NoteAnalysis(
    List<String> keywords,
    String context,
    List<String> tags
) {
    public NoteAnalysis {
        keywords = keywords != null ? List.copyOf(keywords) : List.of();
        tags = tags != null ? List.copyOf(tags) : List.of();
        context = context != null ? context : "";
    }
}
