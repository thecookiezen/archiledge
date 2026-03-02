package com.thecookiezen.archiledger.infrastructure.persistence.ladybugdb.model;

public class LadybugRelation {
    private String id;

    private LadybugEntity targetEntity;

    private String relationType;

    public LadybugRelation() {
    }

    public LadybugRelation(LadybugEntity targetEntity, String relationType) {
        this.targetEntity = targetEntity;
        this.relationType = relationType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LadybugEntity getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(LadybugEntity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }
}
