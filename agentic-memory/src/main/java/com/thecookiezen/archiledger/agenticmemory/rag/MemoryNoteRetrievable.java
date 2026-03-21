package com.thecookiezen.archiledger.agenticmemory.rag;

import com.embabel.agent.rag.model.ContentElement;
import com.embabel.agent.rag.model.Retrievable;
import com.thecookiezen.archiledger.domain.model.MemoryNote;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public record MemoryNoteRetrievable(MemoryNote note) implements Retrievable, ContentElement {

    @Override
    public String getId() {
        return note.id().value();
    }

    @Override
    public String getUri() {
        return null;
    }

    @Override
    public Map<String, Object> getMetadata() {
        Map<String, Object> meta = new HashMap<>();
        meta.put("tags", note.tags());
        meta.put("keywords", note.keywords());
        meta.put("context", note.context() != null ? note.context() : "");
        meta.put("timestamp", note.timestamp());
        meta.put("retrievalCount", note.retrievalCount());
        return Map.copyOf(meta);
    }

    @Override
    public Set<String> labels() {
        return Set.of("MemoryNote", "ContentElement");
    }

    @Override
    public String embeddableValue() {
        return note.content();
    }

    @Override
    public String infoString(Boolean verbose, int indent) {
        String pad = " ".repeat(indent);
        if (Boolean.TRUE.equals(verbose)) {
            return pad + "MemoryNote[" + note.id().value() + "] tags=" + note.tags()
                    + "\n" + pad + "  " + note.content();
        }
        return pad + note.content();
    }
}
