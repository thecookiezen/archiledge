package com.thecookiezen.archiledger.agenticmemory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "cors")
public record CorsProperties(
    boolean enabled,
    boolean allowAnyOrigin,
    List<String> origins,
    List<String> matchOrigins,
    boolean allowCredentials,
    List<String> allowMethods,
    List<String> allowHeaders,
    List<String> exposeHeaders,
    Long maxAge
) {
    public CorsProperties {
        if (origins == null) origins = List.of();
        if (matchOrigins == null) matchOrigins = List.of();
        if (allowMethods == null || allowMethods.isEmpty()) {
            allowMethods = List.of("GET", "POST", "OPTIONS");
        }
        if (allowHeaders == null || allowHeaders.isEmpty()) {
            allowHeaders = List.of(
                "accept", "content-type", "mcp-protocol-version", "mcp-session-id", "traceparent", "tracestate"
            );
        }
        if (exposeHeaders == null || exposeHeaders.isEmpty()) {
            exposeHeaders = List.of(
                "mcp-session-id", "traceparent", "tracestate"
            );
        }
        if (maxAge == null) maxAge = 7200L;
    }
}
