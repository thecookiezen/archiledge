package com.thecookiezen.archiledger.domain.repository;

import java.util.List;

import com.thecookiezen.archiledger.domain.model.MemoryNote;

public interface EmbeddingsService {

    void generateEmbeddings(MemoryNote note);

    List<String> findClosestMatch(String text);

    void deleteEmbeddings(List<String> idList);
}