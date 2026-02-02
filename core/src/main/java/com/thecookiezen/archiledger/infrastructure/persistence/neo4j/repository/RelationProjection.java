package com.thecookiezen.archiledger.infrastructure.persistence.neo4j.repository;

public record RelationProjection(String fromName, String toName, String relationType) {
}
