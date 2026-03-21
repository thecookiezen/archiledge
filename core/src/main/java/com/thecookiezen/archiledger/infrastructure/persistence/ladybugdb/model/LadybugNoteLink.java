package com.thecookiezen.archiledger.infrastructure.persistence.ladybugdb.model;

import org.springframework.data.annotation.Id;

import com.thecookiezen.ladybugdb.spring.annotation.RelationshipEntity;

@RelationshipEntity(type = "LINKED_TO", nodeType = LadybugMemoryNote.class, sourceField = "sourceNote", targetField = "targetNote")
public class LadybugNoteLink {

    @Id
    private String name;

    private LadybugMemoryNote sourceNote;

    private LadybugMemoryNote targetNote;

    private String relationType;

    private String context;

    public LadybugNoteLink() {
    }

    public LadybugNoteLink(String name, LadybugMemoryNote sourceNote, LadybugMemoryNote targetNote,
            String relationType, String context) {
        this.name = name;
        this.sourceNote = sourceNote;
        this.targetNote = targetNote;
        this.relationType = relationType;
        this.context = context;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LadybugMemoryNote getSourceNote() {
        return sourceNote;
    }

    public void setSourceNote(LadybugMemoryNote sourceNote) {
        this.sourceNote = sourceNote;
    }

    public LadybugMemoryNote getTargetNote() {
        return targetNote;
    }

    public void setTargetNote(LadybugMemoryNote targetNote) {
        this.targetNote = targetNote;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
