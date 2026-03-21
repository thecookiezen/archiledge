package com.thecookiezen.archiledger.agenticmemory;

import com.embabel.common.ai.model.LlmOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "agenticmemory")
public record AgenticMemoryProperties(
    @NestedConfigurationProperty LlmOptions chatLlm,
    int neighborsK
) {
    public AgenticMemoryProperties {
        if (neighborsK <= 0) {
            neighborsK = 5;
        }
    }
}
