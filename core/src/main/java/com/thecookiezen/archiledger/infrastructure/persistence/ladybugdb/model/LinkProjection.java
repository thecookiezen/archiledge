package com.thecookiezen.archiledger.infrastructure.persistence.ladybugdb.model;

public record LinkProjection(String fromId, String toId, String relationType, String context) {
}
