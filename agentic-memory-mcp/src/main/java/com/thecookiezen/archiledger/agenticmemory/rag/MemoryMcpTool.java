package com.thecookiezen.archiledger.agenticmemory.rag;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.embabel.agent.mcpserver.McpToolExport;
import com.embabel.agent.rag.service.SearchOperations;
import com.embabel.agent.rag.tools.ToolishRag;

@Configuration
public class MemoryMcpTool {

    @Bean
    McpToolExport ragTools(@Qualifier("archiledgerSearchOperations") SearchOperations searchOperations) {
        var toolishRag = new ToolishRag("memory-notes", "Historical memories for finding related content and establishing connections",
                searchOperations)
            .withSearchFor(List.of(MemoryNoteRetrievable.class));
        
        return McpToolExport.fromLlmReference(toolishRag); 
    }
}
