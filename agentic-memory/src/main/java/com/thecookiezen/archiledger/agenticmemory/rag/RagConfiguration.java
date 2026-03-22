package com.thecookiezen.archiledger.agenticmemory.rag;

import com.thecookiezen.archiledger.application.service.MemoryNoteService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RagConfiguration {

    @Bean
    MemoryNoteSearchOperations archiledgerSearchOperations(MemoryNoteService memoryService) {
        return new MemoryNoteSearchOperations(memoryService);
    }
}