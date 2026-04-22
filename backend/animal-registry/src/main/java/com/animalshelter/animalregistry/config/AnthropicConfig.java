package com.animalshelter.animalregistry.config;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AnthropicConfig {

    @Value("${anthropic.api-key:}")
    private String apiKey;

    @Bean
    public AnthropicClient anthropicClient() {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("========== ANTHROPIC_API_KEY not set - RAG search LLM responses will be unavailable ==========");
            return null;
        }
        AnthropicClient client = AnthropicOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
        log.info("========== Anthropic Claude client initialized ==========");
        return client;
    }
}
