package com.thecookiezen.archiledger.infrastructure.embeddings;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.thecookiezen.archiledger.domain.model.MemoryNote;
import com.thecookiezen.archiledger.domain.repository.EmbeddingsService;

@Service
public class InMemoryEmbeddingsService implements EmbeddingsService {

    private final VectorStore vectorStore;

    public InMemoryEmbeddingsService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void generateEmbeddings(MemoryNote note) {
        vectorStore.add(List.of(
                new Document(note.id().toString(), note.content(), Collections.emptyMap())));
    }

    @Override
    public List<String> findClosestMatch(String text) {
        return vectorStore.similaritySearch(text).stream()
                .map(Document::toString)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteEmbeddings(List<String> idList) {
        vectorStore.delete(idList);
    }
}
