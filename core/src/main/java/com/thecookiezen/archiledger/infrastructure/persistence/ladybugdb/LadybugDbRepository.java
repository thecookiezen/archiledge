package com.thecookiezen.archiledger.infrastructure.persistence.ladybugdb;

import java.util.List;

import com.thecookiezen.archiledger.infrastructure.persistence.ladybugdb.model.LadybugEntity;
import com.thecookiezen.archiledger.infrastructure.persistence.ladybugdb.model.LadybugRelation;
import com.thecookiezen.archiledger.infrastructure.persistence.ladybugdb.model.RelationProjection;
import com.thecookiezen.ladybugdb.spring.annotation.Query;
import com.thecookiezen.ladybugdb.spring.repository.NodeRepository;

public interface LadybugDbRepository extends NodeRepository<LadybugEntity, String, LadybugRelation, LadybugEntity> {
    @Query("MATCH (n:Entity) WHERE n.type = $type RETURN n")
    List<LadybugEntity> findByType(String type);

    @Query("MATCH (source:Entity)-[r:RELATED_TO]->(target:Entity) WHERE source.name = $entityName OR target.name = $entityName RETURN source.name AS fromName, target.name AS toName, r.relationType AS relationType")
    List<RelationProjection> findRelationsForEntity(String entityName);

    @Query("MATCH (source:Entity)-[r:RELATED_TO]->(target:Entity) WHERE r.relationType = $relationType RETURN source.name AS fromName, target.name AS toName, r.relationType AS relationType")
    List<RelationProjection> findRelationsByRelationType(String relationType);

    @Query("MATCH (n:Entity)-[r:RELATED_TO]-(m:Entity) WHERE n.name = $entityName RETURN DISTINCT m AS n")
    List<LadybugEntity> findRelatedEntities(String entityName);

    @Query("MATCH (n:Entity) RETURN DISTINCT n.type AS type")
    List<String> findAllEntityTypes();

    @Query("MATCH (:Entity)-[r:RELATED_TO]->(:Entity) RETURN DISTINCT r.relationType AS relationType")
    List<String> findAllRelationTypes();
}
