package com.thecookiezen.archiledger.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.regex.Pattern;

@Configuration
@EnableConfigurationProperties(CorsProperties.class)
@ConditionalOnProperty(prefix = "cors", name = "enabled", havingValue = "true")
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter(CorsProperties properties) {
        var baseConfig = new CorsConfiguration();

        if (properties.allowAnyOrigin()) {
            baseConfig.addAllowedOriginPattern(CorsConfiguration.ALL);
        } else {
            properties.origins().forEach(baseConfig::addAllowedOrigin);
        }

        if (properties.allowCredentials()) {
            baseConfig.setAllowCredentials(true);
        }

        baseConfig.setAllowedMethods(properties.allowMethods());
        baseConfig.setAllowedHeaders(properties.allowHeaders());
        baseConfig.setExposedHeaders(properties.exposeHeaders());
        
        if (properties.maxAge() != null) {
            baseConfig.setMaxAge(properties.maxAge());
        }

        var matchOriginPatterns = properties.matchOrigins().stream()
                .map(Pattern::compile)
                .toList();

        var staticSource = new UrlBasedCorsConfigurationSource();
        staticSource.registerCorsConfiguration("/**", baseConfig);

        CorsConfigurationSource dynamicSource = request -> {
            var resolved = staticSource.getCorsConfiguration(request);
            if (resolved == null) {
                return null;
            }

            var origin = request.getHeader(HttpHeaders.ORIGIN);
            if (origin != null && !properties.allowAnyOrigin() && !matchOriginPatterns.isEmpty()) {
                var matchesRegex = matchOriginPatterns.stream()
                        .anyMatch(pattern -> pattern.matcher(origin).matches());
                if (matchesRegex) {
                    var dynamicConfig = new CorsConfiguration(resolved);
                    dynamicConfig.addAllowedOrigin(origin);
                    return dynamicConfig;
                }
            }
            return resolved;
        };

        return new CorsFilter(dynamicSource);
    }
}
