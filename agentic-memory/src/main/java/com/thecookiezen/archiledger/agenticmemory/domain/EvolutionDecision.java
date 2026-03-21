package com.thecookiezen.archiledger.agenticmemory.domain;

import java.util.List;

public record EvolutionDecision(
    boolean shouldEvolve,
    List<SuggestedLink> suggestedLinks,
    List<String> updatedTags,
    List<NeighborUpdate> neighborUpdates
) {
    public EvolutionDecision {
        suggestedLinks = suggestedLinks != null ? List.copyOf(suggestedLinks) : List.of();
        updatedTags = updatedTags != null ? List.copyOf(updatedTags) : List.of();
        neighborUpdates = neighborUpdates != null ? List.copyOf(neighborUpdates) : List.of();
    }
}
